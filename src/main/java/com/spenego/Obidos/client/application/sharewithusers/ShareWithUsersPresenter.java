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

package com.spenego.Obidos.client.application.sharewithusers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ObidosPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroup;
import com.spenego.Obidos.client.application.widgets.ObidosInputGroupAddon;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.summernote.Summernote;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.ContainerService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.ItemService;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.PostOpActions;
import com.spenego.Obidos.shared.SharedSetQuality;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.ItemDTO;
import com.spenego.Obidos.shared.dto.LicenseStats;
import com.spenego.Obidos.shared.dto.LimitedUserDTO;
import com.spenego.Obidos.shared.dto.LimitedUserResult;
import com.spenego.Obidos.shared.dto.UserDTO;

public class ShareWithUsersPresenter extends ObidosPresenter<LimitedUserDTO, ShareWithUsersPresenter.MyView, ShareWithUsersPresenter.MyProxy, ShareWithUsersUiHandlers>
        implements ShareWithUsersUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	
    // Issue #201 use multi select
	// Fix #228
    Summernote summernote = new Summernote();

    final String LIST_CONTAINTERS = "List Containers";
    String SHARE_WHAT = "";
    static String sMessage = null;

    interface MyView extends com.spenego.Obidos.client.application.ObidosPresenter.ItemShareView<ShareWithUsersUiHandlers, LimitedUserDTO>
    {
    	BlockQuote getHelpBlockQuote();
        TextBox getShareCommentTextBox();
        TextBox getContainerNameTextBox();
        Row getContainerRow();
        Button getClearButton();
        ObidosInputGroup getNameInputGroup();
        ObidosMessageRow getMessageRow();
        ObidosPanelHeader getPanelHeader();
        ObidosInputGroupAddon getContainerTypeAddon();
        ToggleSwitch getSendNotificationEmailSwitch();
        Label getLicenseLabel();
        ToggleSwitch getSendNotificationSmsSwitch();
        Label getLicenseLabelSms();
   }

    @NameToken(NameTokens.SHARE_WITH_USERS)
    @ProxyCodeSplit
    interface MyProxy extends ProxyPlace<ShareWithUsersPresenter>
    {
    }

	@Override
	protected String getIdName() {
		return ObidosConstants.ITEM_ID;
	}

    @Inject
    ShareWithUsersPresenter(EventBus eventBus, MyView view, MyProxy proxy,
    		final PlaceManager placeManager,
    		final CurrentUser currentUser)
    {
        super(eventBus, view, proxy, placeManager, currentUser);

        getView().setUiHandlers(this);
    }

    protected void onBind()
    {
        super.onBind();
        DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
        grid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
        grid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<LimitedUserDTO> createCheckboxManager());

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler()
        {

            @Override
            public void onSelectionChange(SelectionChangeEvent event)
            {
               Set<LimitedUserDTO> dtos = selectionModel.getSelectedSet();
               if (dtos.size() > 0)
               {
                   enableButtons(true);
               }
               else
               {
                   String title = SHARE_WHAT;
                   enableButtons(false);
                   showTheMessage();
               }
            }
        });
        showUserList(selectionModel,grid, this);
		selectCheckBoxByClickingOnTheRow(selectionModel,grid);
    }

    private void showTheMessage()
    {
    	if (sMessage != null)
    	{
    		showMessage(sMessage);
    		sMessage = null;
    	}
    	else
    	{
    		showMessage("");
    	}
    }

    protected void onReveal()
    {
        super.onReveal();
    }

    protected void onHide()
    {
        super.onHide();
        sMessage = null;
        clearCheckBoxSelections();
    }

    protected void onUnbind()
    {
        super.onUnbind();
    }

    protected void onReset()
    {
        super.onReset();
        ClientUtils.customizeSummernoteToolbar(summernote);
        summernote.setDefaultHeight(100);
        sMessage = null;
        showMessage(null);

        clearCheckBoxSelections();
        checkLicense();
        resetForm();
        showMessage("");
        updateContainerNameInForm();
        updateForm();
        updateTitles();
        refreshDataGrid();
        DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
        
        grid.setColumnWidth(0, "55px");
        
    }

    private void checkLicense()
    {
		CurrentUser cuser = this.currentUser;
		LicenseStats license = null;
		if (cuser != null)
		{
			UserDTO userDTO = cuser.getUserDTO();
			if (userDTO != null)
			{
				license = userDTO.getLicense();
			}
		}
		ToggleSwitch ts = getView().getSendNotificationEmailSwitch();
		ts.setEnabled(true);
		ts.setTitle("");

		boolean supportEmailNotifiation = ClientUtils.doesLicenseSupportEmailNotifiation(license);
		Label label = getView().getLicenseLabel();
		label.setVisible(false);
		String t = glang.notSupportedByLicense();
		if (! supportEmailNotifiation)
		{
			ts.setEnabled(false);
			ts.setTitle(t);
			label.setVisible(true);
			label.setText(t);
		}
		
		ts = getView().getSendNotificationSmsSwitch();
		ts.setEnabled(true);
		ts.setTitle("");
		boolean supportSmsNotification = ClientUtils.doesLicenseSupportSMSNotification(license);
		label = getView().getLicenseLabelSms();
		label.setVisible(false);
		if ( ! supportSmsNotification)
		{
			ts.setEnabled(false);
			ts.setTitle(t);
			label.setVisible(true);
			label.setText(t);
		}
				
	}

    private void updateTitles()
    {
    	String share = ClientUtils.getShareFromUrl(placeManager);
    	
    	if (ObidosConstants.ITEM.equals(share))
    	{
    		getView().getPanelHeader().setHeadingText(glang.shareItemWithUsers());
    	}
    	else if (ObidosConstants.NOTE.equals(share))
    	{
    		getView().getPanelHeader().setHeadingText(glang.shareNoteWithUsers());
    		
    	}
    }


    /*
    private String getShareFromUrl()
    {
        String share = null;
        String key = ObidosConstants.SHARE;
        try
        {
            share = ClientUtils.getParameterFromUrl(placeManager, key);
            return share;
        } catch (ParamNotFoundException e)
        {
            showErrorMessage("Could not get " + key + " from URL");
        }
        return null;
    }*/

    private void updateNameLabel(String text)
    {
        getView().getNameLabel().setText(text);
    }

    // not used anymore as we use multi select users/groups
    private void updateShareButtonTitle(String title)
    {
//        getView().getShareButton().setText(title);
    }

    private void enableButtons(boolean enabled)
    {
        getView().getShareButton().setEnabled(enabled);
        getView().getClearButton().setEnabled(enabled);
    }

    private void updateListButtonTitle(String text)
    {
    }

    private void updatePanelHeading(String text)
    {
        getView().getPanelHeader().setHeadingText(text);
    }

    private void updateForm()
    {
    	ObidosMessages lang = ObidosMessages.LANG;
        updateNameLabel("Name?");
        updateShareButtonTitle("Share?");
//        updateListButtonTitle("List?");

        String share = ClientUtils.getShareFromUrl(placeManager);
        if (share == null)
        {
            return;
        }
        Long itemId = getItemIdFromUrl();
        Long contaienrId = getContainerIdFromUrl();
        updateShareButtonTitle(lang.share());

        if (itemId != null) // this is also true for note
        {
            if (ObidosConstants.NOTE.equals(share))
            {
                updateNameLabel(ObidosMessages.LANG.noteName());
            	updatePanelHeading(lang.shareNoteWithUsers());
            }
            if (ObidosConstants.ITEM.equals(share))
            {
                updateNameLabel(ObidosMessages.LANG.itemName());
            	updatePanelHeading(lang.shareItemWithUsers());
            }
            fetchAndPopulateForm();
        }
        else if (contaienrId != null)
        {
            updateNameLabel(ObidosMessages.LANG.containerNameLabel());
            updatePanelHeading(lang.shareContainerWithUsers());
            updateListButtonTitle(lang.listContainers());
        }
        else
        {
            showErrorMessage("Could not update form. Could not get " + ObidosConstants.ITEM_ID + " or " + ObidosConstants.CONTAINER_ID + " from URL");
            return;
        }
    }

    private void resetForm()
    {
        showMessage("");
        getView().getSearchTextBox().setValue("");
        getView().getShareCommentTextBox().setValue("");
        getView().getSendNotificationEmailSwitch().setValue(false);
        getView().getSendNotificationSmsSwitch().setValue(false);
        enableButtons(false);
    }

    private void showUserList(final SelectionModel<LimitedUserDTO> selectionModel,final AbstractCellTable<LimitedUserDTO> grid, HasHandlers source)
    {
    	ObidosMessages lang = ObidosMessages.LANG;
        grid.setAutoHeaderRefreshDisabled(true);
	    grid.setAutoFooterRefreshDisabled(true);

	    Code messageLabel = new Code();
	    messageLabel.setText("Loading ...");
	    grid.setEmptyTableWidget(messageLabel);

	    // Checkbox
        addCheckBoxColumn(grid, dto -> selectionModelValue(selectionModel, dto));

        // fullname
        ObidosButtonCell fullnameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_FULLNAME);
        final Column<LimitedUserDTO, String> fullnameCol = new Column<LimitedUserDTO, String>(fullnameCell)
        {

            @Override
            public String getValue(LimitedUserDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(fullnameCol, lang.fullname());

        
        // email
        ObidosButtonCell emailCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_EMAIL);
        final Column<LimitedUserDTO, String> emailCol = new Column<LimitedUserDTO, String>(emailCell)
        {

            @Override
            public String getValue(LimitedUserDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(emailCol, lang.email());

        AsyncDataProvider<LimitedUserDTO> dataProvider = new AsyncDataProvider<LimitedUserDTO>()
        {

            @Override
            protected void onRangeChanged(HasData<LimitedUserDTO> dto)
            {
                final Range range = dto.getVisibleRange();
                final int start = range.getStart();
                int length = range.getLength();
                gwtLog(" in AsyncDataProvider start: " + start);
                gwtLog("in AsyncDataProvider length: " + length);
                GwtAsyncWrapper<LimitedUserResult> callback = new GwtAsyncWrapper<LimitedUserResult>(source)
                {

                    @Override
                    public void uponFailure(Throwable caught)
                    {
                        gwtLog("Exception caught: " + caught.getMessage());
                        updateRowCount(0, true);
                        messageLabel.setText("");
                    }

                    @Override
                    public void uponSuccess(LimitedUserResult userResult)
                    {
                        int numberOfUsers = userResult.getTotalUsers();
                        gwtLog("+++++++ Found number of users: " + numberOfUsers);
                        List<LimitedUserDTO> users = userResult.getUsers();
                        if (users != null && users.size() > 0)
                        {
                        	gwtLog(">> returned " + users.size() + " users");
                            updateRowCount(numberOfUsers, true);
                            updateRowData(start, users);
                        }
                        else
                        {
                            updateRowCount(0, true);
                            messageLabel.setText("");
                        }
                    }

                };
                String searchString = getView().getSearchTextBox().getValue();
                if (searchString.length() == 0)
                {
                    searchString = null;
                }
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                UserDTO userDTO = new UserDTO();
                userDTO.setDeleted(false);
                // Allow interact with locked users
                // set to null for both unlocked and locked users
                // Issue # 465
                userDTO.setLocked(null); 
                userDTO.setAdministrator(false);
                // Issue #387
                userDTO.setFullname(searchString);
				final ArrayList<Long> preSelectedUsers = getSelectedIds((MultiSelectionModel<LimitedUserDTO>) selectionModel);
                UserService.Utility.getInstance().getUsersForItem(authCreds, getId(), userDTO, SharedSetQuality.NOT_SHARED_WITH, preSelectedUsers, start, length, toArray(OrderBy.FULLNAME_ASC), callback);
            }
        };
        
        
        fullnameCol.setFieldUpdater(new FieldUpdater<LimitedUserDTO, String>()
        {

            @Override
            public void update(int idx, LimitedUserDTO dto, String value)
            {
            	showUserInfo(dto);
            }
        });


        // noop but the handler must exist
        emailCol.setFieldUpdater(new FieldUpdater<LimitedUserDTO, String>()
        {

            @Override
            public void update(int idx, LimitedUserDTO dto, String value)
            {
            }
        });

        getView().getPager().setDisplay(grid);
        dataProvider.addDataDisplay(grid);
    }

    /* private void showInfoMessgge(String message)
    {
    	showMessage(message);
    } */

    public void refreshDataGrid()
    {
        DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
    }

    @Override
    public void searchUsersToShare()
    {
    	refreshDataGrid();
    }

    @Override
    public void showListItemsPage()
    {
        String nameToken = NameTokens.LIST_ITEMS;
    	ClientUtils.navigateToPlace(placeManager, nameToken);
    }

    private ArrayList<Long> getUserIds(Set<LimitedUserDTO> users) {
    	final ArrayList<Long> list = new ArrayList<Long>(users.size());
    	for(final LimitedUserDTO u : users) { list.add(u.getId()); }
    	return list;
    }

    @Override
    public void share()
    {
        Set<LimitedUserDTO> dtoSet = selectionModel.getSelectedSet();
        if (dtoSet.size() == 0)
        {
            return;
        }

        String shareComment = getView().getShareCommentTextBox().getValue();
        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponSuccess(Void result)
            {
                Date date = new Date();
				if (!dtoSet.isEmpty())
				{
					int sz = dtoSet.size();
					String itemName = getView().getNameInputGroup().getText();
					if (sz == 1)
					{
//						sMessage = "Item shared with " + fullname + " on " + date.toString();
						String fullname = dtoSet.iterator().next().getFullname();
						sMessage = ObidosMessages.LANG.item() + " " + "<span style=\"color: purple;\">" + 
                                                           "'" +itemName + "'"+ "</span>" + " shared with " + 
                                                           "<span style=\"color: purple;\">" + "'" + fullname + "'" + "</span>"+".";
					}
					else
					{
//						sMessage = "Item shared with " + sz + " users on " + date.toString();
						sMessage = ObidosMessages.LANG.item() + " " + "<span style=\"color: purple;\">" + 
                                                           "'" +itemName + "'"+ "</span>" + " shared with " + 
                                                           "<span style=\"color: purple;\">" + sz + "</span>"+" users.";
					}
				}
				else
				{
					showErrorMessage("No selected users found"); // should not be
				}

                gwtLog(sMessage);
               	getView().getSearchTextBox().setValue("");
				// must clear CheckBox selections or they will accumulate
                clearCheckBoxSelections();
                refreshDataGrid();
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage("Could not share item: " + caught.getMessage());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        PostOpActions postShareActions = null;
        if (getView().getSendNotificationEmailSwitch().getValue())
        {
        	gwtLog("Send email about sharing..........");
        	if (postShareActions == null)
        	{
        		postShareActions = new PostOpActions();
        	}
        	postShareActions.setPostOpActions(PostOpActions.SEND_EMAIL);
        }

       	// Issue #874 03/12/2024
        if (getView().getSendNotificationSmsSwitch().getValue())
        {
        	gwtLog("Send SMS about sharing ...");
        	if (postShareActions == null)
        	{
        		postShareActions = new PostOpActions();
        	}
        	postShareActions.setPostOpActions(PostOpActions.SEND_SMS);
        }
        ItemService.Utility.getInstance().shareItemWithUsers(authCreds, getId(), getUserIds(dtoSet), shareComment, postShareActions, callback);
    }
    private void updateContainerNameInForm()
    {
        Long containerId = getContainerIdFromUrl();
        if (containerId == null)
        {
            showContainerGroup(false);
            return;
        }

        showContainerGroup(true);

        GwtAsyncWrapper<ContainerDTO> callback = new GwtAsyncWrapper<ContainerDTO>(this)
        {

            @Override
            public void uponSuccess(ContainerDTO result)
            {
            	ObidosInputGroupAddon containerTypeAddon = getView().getContainerTypeAddon();
                boolean privateContainer = ClientUtils.fromBoolean(result.getIsPrivate());
                if (privateContainer)
                {
					ClientUtils.setTypeAddon(containerTypeAddon, false);
                }
                else
                {
					ClientUtils.setTypeAddon(containerTypeAddon, true);
                }
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage("Could not fetch Container");
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        ContainerService.Utility.getInstance().get(authCreds, containerId, callback);
    }
    private void showContainerGroup(boolean visible)
    {
        getView().getContainerRow().setVisible(visible);
    }

    private void fetchAndPopulateForm()
    {
        Long itemId = getItemIdFromUrl();
        if (itemId == null)
        {
            return;
        }

        GwtAsyncWrapper<ItemDTO> callback = new GwtAsyncWrapper<ItemDTO>(this)
        {
            @Override
            public void uponSuccess(ItemDTO result)
            {
            	gwtLog("Fucking cache..");
            	getView().getNameInputGroup().setText(result.getName());
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage("Could not get Item: " + caught.getMessage());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        ItemService.Utility.getInstance().get(authCreds, itemId, toArray(OrderBy.UDF_POSITION_ASC), callback);
    }

    @Override
    public void clearCheckBoxSelections()
    {
    	clearCheckBoxSelections(selectionModel);
    }

	@Override
	public void help()
	{
		showHelp();
	}

	@Override
	protected BlockQuote getHelpBlockQuote()
	{
		return getView().getHelpBlockQuote();
	}

	private void showUserInfo(final LimitedUserDTO dto)
	{
		ClientUtils.showUserInfo(placeManager, dto.getId(), getView().getMessageRow());

	}

	@Override
	public void back()
	{
			ClientUtils.goBack(placeManager);
	}

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}
}
