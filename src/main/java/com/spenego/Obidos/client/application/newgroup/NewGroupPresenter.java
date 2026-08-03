/*
 * Copyright (C) 2016 - 2026 Spenego Software LLC. All rights reserved.
 *
 * This file is part of Obidos from Spenego Software LLC
 *
 * Obidos is dual-licensed under a commercial license and the GNU
 * Affero General Public License (AGPL) v3.0. For commercial licensing,
 * contact Spenego Software LLC at https://spenego.com/contacts.html.
 *
 * For AGPL licensing terms, see the LICENSE file in the project root
 * or <https://www.gnu.org/licenses/>.
 */

package com.spenego.Obidos.client.application.newgroup;

import java.util.Date;
import java.util.EnumMap;
import java.util.Iterator;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.ListBox;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosResetButton;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseCanCreateShareGateKeepr;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.GroupDTO;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

public class NewGroupPresenter extends Presenter<NewGroupPresenter.MyView, NewGroupPresenter.MyProxy>
        implements NewGroupUiHandlers
{
    private GroupDTO groupDTO;
    private ObidosMessages glang = ObidosMessages.LANG;

    private enum FieldNumber
    {
        groupName,
        groupComment
    };

    private static EnumMap<FieldNumber, Boolean> gEMap = new EnumMap<FieldNumber,Boolean>(FieldNumber.class);
    
    private void resetFieldsMaps()
    {
    	for (FieldNumber fn : FieldNumber.values())
    	{
    		gEMap.put(fn, false);
    	}
    }
    private void resetLabelColors()
    {
        setFormLabelColorToOriginal(getView().getGroupNameLabel());
        setFormLabelColorToOriginal(getView().getGroupCommentLabel());
    }

    private void setFormLabelColorToOriginal(FormLabel label)
    {
        ClientUtils.setFormLabelsColorOriginal(label);
    }
    private void setFormLabelColorToChanged(FormLabel label)
    {
        ClientUtils.setFormLabelsColorChanged(label);
    }


    interface MyView extends View, HasUiHandlers<NewGroupUiHandlers>
    {
        public FormLabel getGroupNameLabel();
        public FormLabel getGroupCommentLabel();
        public ObidosPanelHeader getPanelHeader();
        public Button getSaveButton();
        public BlockQuote getHelpBlockQuote();
        public ObidosMessageRow getMessageRow();
        public ObidosButtonToolBar getBottomToolBar();
        public ObidosInputGroup getGroupNameInputGroup();
        public ObidosResetButton getResetButton();
        public ObidosInputGroup getGroupCommentInputGroup();
       	public ListBox getLanguageListBox();
       	public ObidosRowBottom2px getLanguageRow();
    }
    
    @NameToken(NameTokens.NEW_GROUP)
    @ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseCanCreateShareGateKeepr.class)
    interface MyProxy extends ProxyPlace<NewGroupPresenter>
    {
    }

    final PlaceManager placeManager;
    final CurrentUser currentUser;

    @Inject
    NewGroupPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            CurrentUser currentUser)
    {
        super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);

        this.placeManager = placeManager;
        this.currentUser = currentUser;

        getView().setUiHandlers(this);
    }

    protected void onBind()
    {
        super.onBind();
    }

    protected void onReveal()
    {
        super.onReveal();
    }

    protected void onHide()
    {
        super.onHide();
        ClientUtils.resetLanguage(getView().getLanguageRow());
    }

    protected void onUnbind()
    {
        super.onUnbind();
    }

    protected void onReset()
    {
        super.onReset();

        if (ClientUtils.isAdmin(currentUser)) return;

        if (!ClientUtils.isPassphraseRegistered(currentUser))
        {
        	ClientUtils.showRegisterPassphraseDialog(glang.createGroup(),
        			()->ClientUtils.goBack(placeManager), 
        			()->ClientUtils.goToRegisterPassphrasePage(placeManager, NameTokens.NEW_GROUP));
        	return;
        }

        showMessage("");
        getView().getBottomToolBar().adjustButtonsWidth();
        updateBackButtonLabel();
        resetFieldsMaps();
        resetForm();
        updateForm();
        fetchGroup();
        ClientUtils.focusToWidegt(getView().getGroupNameInputGroup().getTextBox());
		ClientUtils.showLanguageListBox(getView().getLanguageRow(), getView().getLanguageListBox());
    }

    private void resetForm()
    {
        getView().getGroupNameInputGroup().setText(null);
        getView().getGroupCommentInputGroup().setText(null);
        if (!isEditing())
        {
        	// create
        	showResetButton(false);
        	enableResetButton(false);
        	enableSaveButton(true);
        }
        else
        {
        	showResetButton(true);
        	enableResetButton(false);
        	enableSaveButton(false);
        	resetLabelColors();
        }
    }
    
    private void enableSaveButton(boolean enabled)
    {
    	getView().getSaveButton().setEnabled(enabled);
    }
    
    private void enableResetButton(boolean enabled)
    {
    	getView().getResetButton().setEnabled(enabled);
    }
    
    private void showResetButton(boolean visible)
    {
    	getView().getResetButton().setVisible(visible);
    }

    private void showMessage(String message)
    {
    	getView().getMessageRow().showMessage(message);
    }

    private void updateForm()
    {
        if (isEditing())
        {
            enableResetButton(true);
            updatePanelHeading(glang.updateGroup());
            updateSaveButtonTitle(glang.update());
            updateGroupNameLabel(glang.groupname());
        }
        else
        {
            enableResetButton(false);
            updatePanelHeading(glang.createNewGroup());
            updateSaveButtonTitle(glang.create());
            updateGroupNameLabel(glang.groupNameLabelHtml());
        }

        String ng = null;
        try
        {
            ng = ClientUtils.getParameterFromUrl(placeManager,ObidosConstants.NUMBER_OF_GROUP);
        } catch (ParamNotFoundException e)
        {
        }
    }

    /*
    private void showInfoMessage()
    {
            showInfoPanel();
            String html =
"<i class=\"fa fa-info-circle\"></i>&nbsp;" +
"You do not have any Groups, please create one";

            getView().getMessageSpan().setHTML(html);

    }
    */

    private void updateSaveButtonTitle(String title)
    {
        getView().getSaveButton().setText(title);
    }

    public void fetchGroup()
    {
        Long groupId = null;
        try
        {
            groupId = ClientUtils.getIdFromUrl(placeManager, ObidosConstants.GROUP_ID);
        } catch (NumberFormatException | ParamNotFoundException e)
        {
//            showErrorMessage("Could not get groupid from URL");
            return;
        }
        gwtLog("groupid: " + groupId);
        GwtAsyncWrapper<GroupDTO> callback = new GwtAsyncWrapper<GroupDTO>(this)
        {

            @Override
            public void uponFailure(Throwable e)
            {
                showErrorMessage("Could not fetch Group: " + e.getMessage());
            }

            @Override
            public void uponSuccess(GroupDTO dto)
            {
                resetForm();

                getView().getGroupNameInputGroup().setText(dto.getName());
                getView().getGroupCommentInputGroup().setText(dto.getComments());
                setOriginalGroupDTO(dto);
                disableModifyButton();
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().getGroup(authCreds, groupId, callback);
    }

    private void updateGroupNameLabel(String text)
    {
        getView().getGroupNameLabel().setHTML(text);
    }

    private void updatePanelHeading(String heading)
    {
        getView().getPanelHeader().setHeadingText(heading);
    }

    private void showMessgae(String message)
    {
    	getView().getMessageRow().showMessage(message);
    }

    private void showErrorMessage(String errorMessage)
    {
    	getView().getMessageRow().showErrorMessage(errorMessage);
    }

    private void updateGroup(Long groupId, String groupName, String comment)
    {
        GroupDTO dto = new GroupDTO();
        dto.setId(groupId);
        dto.setName(groupName);
        dto.setComments(comment);

        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponFailure(Throwable e)
            {
                showErrorMessage("Could not upddate Group: " + e.getMessage());
            }

            @Override
            public void uponSuccess(Void arg0)
            {
            	fetchGroup();
            	String date = ClientUtils.formattedDate(new Date());
            	showMessage("Group updated on: " + date);
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().updateGroup(authCreds,dto,callback);
    }
    
    private void clearForm()
    {
    	getView().getGroupNameInputGroup().setText("");
    	getView().getGroupCommentInputGroup().setText("");
    }

    @Override
    public void createUpdateGroup()
    {
        Long groupId = null;
        String groupName = getView().getGroupNameInputGroup().getText();
        if (groupName.length() == 0)
        {
        	showErrorMessage(glang.specifyGroupName());
            return;
        }

        String comment = getView().getGroupCommentInputGroup().getText();

        try
        {
            groupId = ClientUtils.getIdFromUrl(placeManager, ObidosConstants.GROUP_ID);
            updateGroup(groupId,groupName,comment);
            return;
        } catch (NumberFormatException | ParamNotFoundException e)
        {
        }


        GwtAsyncWrapper<Long> callback = new GwtAsyncWrapper<Long>(this)
        {

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage("Could not create group: " + caught.getMessage());
            }

            @Override
            public void uponSuccess(Long result)
            {
                String groupName = getView().getGroupNameInputGroup().getText();
//            	showMessage("Group created successfully on: " + new Date().toString());
                showMessage(glang.groupCreatedSuccessFully(groupName));
            	clearForm();
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().createGroup(authCreds, groupName, comment, callback);
    }
    
    private boolean isEditing()
    {
    	String action = ClientUtils.getActionFromUrl(placeManager);
    	if (action != null && ClientUtils.getGroupIdFromUrl(placeManager) != null)
    	{
    		if (ObidosConstants.EDIT.equals(action))
    		{
    			return true;
    		}
    		return false;
    	}
    	return false;
    }

    private void formValueChanged(String origVal, String formVal, FormLabel label, FieldNumber fieldNumber)
    {
    	showMessage(null);
        boolean dirty = false;
        if (origVal != null && formVal != null && formVal.length() == 0)
        {
            dirty = true;
        }
        if (origVal == null && (formVal != null && formVal.length() > 0))
        {
            dirty = true;
        }

        if (origVal == null)
        {
            gwtLog("origVal is null dirty: " + dirty);
        }

        if (origVal != null && !origVal.equals(formVal))
        {
            gwtLog("Should not be here. origVal: " + origVal + " formVal: " + formVal);
            dirty = true;
        }
        if (formVal != null && formVal.length() == 0)
        {
            dirty = false;
        }
        if (fieldNumber == FieldNumber.groupComment)
        {
        	if (formVal != null && formVal.length() == 0)
        	{
        		if (origVal != null && origVal.length() > 0)
        		{
        			dirty = true;
        		}
        	}
        }

        gwtLog("formval: " + formVal);

        gEMap.put(fieldNumber, dirty);
        if (dirty)
        {
            ClientUtils.setFormLabelsColorChanged(label);
        }
        else
        {
            ClientUtils.setFormLabelsColorOriginal(label);
        }
        enableDisableButtons();
    }

    private void enableDisableButtons()
    {
        Iterator<FieldNumber> enumKeySet = gEMap.keySet().iterator();
        boolean dirty = false;
        while(enumKeySet.hasNext())
        {
            FieldNumber fieldNumber = enumKeySet.next();
            boolean val = gEMap.get(fieldNumber);
            if (val)
            {
                dirty = true;
                break;
            }
        }
        if (dirty)
        {
        	enableSaveButton(true);
        	enableResetButton(true);
        }
        else
        {
        	enableSaveButton(false);
        	enableResetButton(false);
        }
    }

    private GroupDTO getOriginalGroupDTO()
    {
        return groupDTO;
    }

    private void setOriginalGroupDTO(GroupDTO dto)
    {
        this.groupDTO = dto;
    }

    private void enableModifyButton()
    {
        getView().getSaveButton().setEnabled(true);
    }
    private void disableModifyButton()
    {
        getView().getSaveButton().setEnabled(false);
    }

    @Override
    public void showGroupNameChange()
    {
    	showMessage(null);
    	if (!isEditing())
    	{
    		return;
    	}
        GroupDTO dto = getOriginalGroupDTO();
        if (dto == null)
        {
            return;
        }
        FormLabel label = getView().getGroupNameLabel();
        String ov = dto.getName();
        String nv = getView().getGroupNameInputGroup().getText();
        formValueChanged(ov, nv, label, FieldNumber.groupName);
    }

    @Override
    public void showGroupCommentChange()
    {
    	showMessage(null);
    	if (!isEditing())
    	{
    		return;
    	}
        GroupDTO dto = getOriginalGroupDTO();
        if (dto == null)
        {
            return;
        }
        FormLabel label = getView().getGroupCommentLabel();
        String ov = dto.getComments();
        String nv = getView().getGroupCommentInputGroup().getText();
        formValueChanged(ov, nv, label, FieldNumber.groupComment);

    }

    public void showHideHelp()
    {
    	ClientUtils.showHelp(getView().getHelpBlockQuote());
    }

	@Override
	public void back()
	{
		if (isEditing())
		{
			ClientUtils.back(placeManager);
		}
		else
		{
			ClientUtils.showPage(placeManager, NameTokens.LIST_GROUPS);
		}
	}

    private void updateBackButtonLabel()
	{
		Button backButton = getView().getPanelHeader().getBackButton();
		if (isEditing())
		{
			backButton.setText(glang.back());
		}
		else
		{
			backButton.setText(glang.groups());
		}
	}
	
	private void gwtLog(String message)
	{
		ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
	}

	@Override
	public void reset()
	{
		fetchGroup();
	}

	@Override
	public void languageListBoxCallback()
	{
		ListBox lb = getView().getLanguageListBox();
		String lang = lb.getSelectedValue();
		int idx = lb.getSelectedIndex();
		gwtLog("index: "+ idx);
		gwtLog("Lang: " + lang);
		// only support English and Bangla for editing at this time
		switch(idx)
		{
			case 0: // English
			{
				ClientUtils.enableBanglaEditing(false, getView().getLanguageRow());
				break;
			}
			case 1: // Bangla
			{
				ClientUtils.enableBanglaEditing(true, getView().getLanguageRow());
				break;
			}
		}
	}

}
