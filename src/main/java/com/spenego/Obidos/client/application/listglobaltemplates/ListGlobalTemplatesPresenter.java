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

package com.spenego.Obidos.client.application.listglobaltemplates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionModel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ObidosPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosClearSelectionsButton;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.TemplateService;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeResult;

public class ListGlobalTemplatesPresenter
        extends ObidosPresenter<UserDefinedTypeDTO, ListGlobalTemplatesPresenter.MyView, ListGlobalTemplatesPresenter.MyProxy, ListGlobalTemplatesUiHandlers>
        implements ListGlobalTemplatesUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	
    private enum FieldNumber
    {
        editCol,
        copyCol,
        viewCol
    };

    interface MyView extends View, HasUiHandlers<ListGlobalTemplatesUiHandlers>
    {
    	public BlockQuote getHelpBlockQuote();
        public FormLabel getFormErrorLabel();
        public DataGrid<UserDefinedTypeDTO> getDataGrid();
        public SimplePager getPager();
        public TextBox getSearchTextBox();
        public Button getSearchButton();
        public Button getHelpButton();
        public Button getDeleteButton();
        public ObidosMessageRow getMessageRow();
        public ObidosClearSelectionsButton getClearButton();
       	public ListBox getLanguageListBox();
       	public ObidosRowBottom2px getLanguageRow();

    }

    @NameToken(NameTokens.LIST_GLOBAL_TEMPLATES)
    @ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
    interface MyProxy extends ProxyPlace<ListGlobalTemplatesPresenter>
    {
    }

    private final PlaceManager placeManager;
    private final CurrentUser currentUser;

    @Inject
    ListGlobalTemplatesPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            CurrentUser currentUser)
    {
        super(eventBus, view, proxy, placeManager, currentUser);

        this.placeManager = placeManager;
        this.currentUser = currentUser;

        getView().setUiHandlers(this);
    }

    protected void onBind()
    {
        super.onBind();
    	onBind(() -> getView().getDataGrid(), dto -> enableButtons(dto.size() > 0), grid -> showTypeTemplates(selectionModel, grid, this));
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
        showMessage("");
        clearselections();
        updateForms();
        refreshDataGrid();
        DataGrid<UserDefinedTypeDTO> grid = getView().getDataGrid();
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
        setColumnWidth(grid, 0, "55px");  // Checkbox column
		ClientUtils.showLanguageListBox(getView().getLanguageRow(), getView().getLanguageListBox());
    }
    
    private void enableButtons(boolean enabled)
    {
    	boolean canEdit = this.currentUser.getUserDTO().getCapabilities().getCreateGlobalTemplate();
    	Button deleteButton = getView().getDeleteButton();
    	deleteButton.setTitle("");
    	getView().getClearButton().setEnabled(enabled);
    	if (! canEdit)
    	{
    		deleteButton.setTitle(glang.noDeleteToolTip());

    		// Community Bug# 16
    		getView().getDeleteButton().setEnabled(false);
    		return;
    	}
    	getView().getDeleteButton().setEnabled(enabled);
    }

    private void updateForms()
    {
    }

    public void refreshDataGrid()
    {
        DataGrid<UserDefinedTypeDTO> grid = getView().getDataGrid();
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
    }

    private void showTypeTemplates(final SelectionModel<UserDefinedTypeDTO> selectionModel, 
    		final AbstractCellTable<UserDefinedTypeDTO> grid, HasHandlers source)
    {
        grid.setAutoHeaderRefreshDisabled(true);
        grid.setAutoFooterRefreshDisabled(true);

        Code messageLabel = new Code();
        messageLabel.setText("Loading ...");
        grid.setEmptyTableWidget(messageLabel);

        // Select CheckBox
        addCheckBoxColumn(grid, dto -> selectionModelValue(selectionModel, dto));
        
        // Template Name column
        ObidosButtonCell nameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_TEMPLATE_NAME);
        final Column<UserDefinedTypeDTO, String> nameCol = new Column<UserDefinedTypeDTO, String>(nameCell)
        {

            @Override
            public String getValue(UserDefinedTypeDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(nameCol, glang.templateNameLabel());

        // Edit column. Issue #704
        ObidosButtonCell editableCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_EDIT_GLOBAL_TEMPLATE, currentUser);
        final Column<UserDefinedTypeDTO, String> editCol = new Column<UserDefinedTypeDTO, String>(editableCell)
        {

            @Override
            public String getValue(UserDefinedTypeDTO object)
            {
                return glang.edit();
            }
        };
        grid.addColumn(editCol, ObidosMessages.LANG.editTemplate());


        // Copy
        ObidosButtonCell bCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_COPY);
        final Column<UserDefinedTypeDTO, String> copyCol = new Column<UserDefinedTypeDTO, String>(bCell)
        {

            @Override
            public String getValue(UserDefinedTypeDTO object)
            {
                return ObidosMessages.LANG.copyButtonTitle();
            }
        };
        grid.addColumn(copyCol, ObidosMessages.LANG.copyToPersonalTemplate());

        AsyncDataProvider<UserDefinedTypeDTO> dataProvider = new AsyncDataProvider<UserDefinedTypeDTO>()
        {

            @Override
            protected void onRangeChanged(HasData<UserDefinedTypeDTO> dto)
            {
                final Range range = dto.getVisibleRange();
                final int start = range.getStart();
                int length = range.getLength();

                GwtAsyncWrapper<UserDefinedTypeResult> callback = new GwtAsyncWrapper<UserDefinedTypeResult>(source)
                {
                    @Override
                    public void uponFailure(Throwable t)
                    {
                        showErrorMessage(glang.couldNotFetchTemplates(t.getMessage()));
                    }

                    @Override
                    public void uponSuccess(UserDefinedTypeResult result)
                    {
                        int n = result.getTotalTypes();
                        gwtLog("Number of templates: " + n);
                        if (n == 0)
                        {
                            messageLabel.setText("Could not fetch any Personal Templates");
                            updateRowCount(0, true);
                            return;
                        }
                        List<UserDefinedTypeDTO> list = result.getTypes();
                        if (list != null && list.size() > 0)
                        {
                            updateRowCount(n, true);
                            updateRowData(start, list);

                            for (UserDefinedTypeDTO dd:list)
                            {
                                gwtLog(">>>>>>>>>>>>> " + dd.getName());
                            }
                        }
                        else
                        {
                            messageLabel.setText("No Personal Templates found");
                            updateRowCount(0, true);
                        }
                    }
                };
                String searchStr = getView().getSearchTextBox().getValue();
                String search = (searchStr == null || searchStr.length() == 0) ? null : searchStr;
                Long userId = null;
                boolean personal = false;
                List<Long> preSelectedTemplates = null;
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                safeInvocationCall(() -> TemplateService.Utility.getInstance().getUserDefinedTypes(authCreds, personal, userId, search, preSelectedTemplates, start, length, getOrderByList(), callback));
            }
        };

        getView().getPager().setDisplay(grid);
        dataProvider.addDataDisplay(grid);

        // handler for Edit button
        editCol.setFieldUpdater(new FieldUpdater<UserDefinedTypeDTO, String>()
        {

            @Override
            public void update(int index, UserDefinedTypeDTO dto, String value)
            {
                gwtLog("Edit Template: " + dto.getName());
                sendToCorrectPlace(dto, FieldNumber.editCol);
            }
        });

        // handler for Copy button
        copyCol.setFieldUpdater(new FieldUpdater<UserDefinedTypeDTO, String>()
        {

            @Override
            public void update(int index, UserDefinedTypeDTO dto, String value)
            {
                gwtLog("Copy Template: " + dto.getName());
                sendToCorrectPlace(dto, FieldNumber.copyCol);
            }
        });


        nameCol.setFieldUpdater(new FieldUpdater<UserDefinedTypeDTO, String>()
        {

            @Override
            public void update(int index, UserDefinedTypeDTO dto, String value)
            {
                gwtLog("View Template: " + dto.getName());
                sendToCorrectPlace(dto, FieldNumber.viewCol);
            }
        });
    }
    

    protected void deleteTemplate(UserDefinedTypeDTO dto)
    {
        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponSuccess(Void result)
            {
                refreshDataGrid();
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage(glang.couldNotDeleteTemplate() + ": " + caught.getMessage());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        TemplateService.Utility.getInstance().deleteUserDefinedType(authCreds, toArray(dto.getId()), callback);
    }

    private void deleteTemplatesReal()
    {
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		if (ids == null || ids.size() == 0)
		{
			showErrorMessage(glang.noTempaltesSelected());
			return;
		}
        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponSuccess(Void result)
            {
            	gwtLog("Template deleted ...");
            	clearselections();
                refreshDataGrid();
            }

            @Override
            public void uponFailure(Throwable caught)
            {
            	gwtLog("Could not delete template: " + caught.getMessage());
                showErrorMessage(glang.couldNotDeleteTemplate() + ": " + caught.getMessage());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        TemplateService.Utility.getInstance().deleteUserDefinedType(authCreds, ids, callback);
    }

    private void showCopyTemplatePage(UserDefinedTypeDTO dto)
    {
        String nameToken = NameTokens.COPY_TEMPLATE;
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.TEMPLATE_ID, dto.getId().toString());
        with.put(ObidosConstants.TEMPLATE_TYPE,ObidosConstants.GLOBAL);
        ClientUtils.showPage(placeManager, nameToken, with);
    }
    
    private void showViewTemplatePage(UserDefinedTypeDTO dto)
    {
        String templateType = ObidosConstants.GLOBAL;
        String nameToken = NameTokens.EDIT_TEMPLATE;
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.TEMPLATE_ID, dto.getId().toString());
        with.put(ObidosConstants.TEMPLATE_TYPE, templateType);
        with.put(ObidosConstants.ACTION, ObidosConstants.VIEW);
        ClientUtils.showPage(placeManager, nameToken, with);
    }


    private void showEditTemplatePage(UserDefinedTypeDTO dto)
    {
        String templateType = ObidosConstants.GLOBAL;
        String nameToken = NameTokens.EDIT_TEMPLATE;
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.TEMPLATE_ID, dto.getId().toString());
        with.put(ObidosConstants.TEMPLATE_TYPE, templateType);
        with.put(ObidosConstants.ACTION, ObidosConstants.EDIT);
        ClientUtils.showPage(placeManager, nameToken, with);

    }

    private void sendToCorrectPlace(UserDefinedTypeDTO dto, FieldNumber fieldNumber)
    {
        GwtAsyncWrapper<Boolean> callback = new GwtAsyncWrapper<Boolean>(this)
        {
            @Override
            public void uponFailure(Throwable t)
            {
            }

            @Override
            public void uponSuccess(Boolean rc)
            {
                gwtLog("passphrase cached: " + rc);
                if (rc)
                {
                    currentUser.setPassphraseRegistered(Boolean.TRUE);
                }
                else
                {
                    currentUser.setPassphraseRegistered(Boolean.FALSE);
                }
                switch(fieldNumber)
                {
                    case editCol:
                    {
                		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
                		{
                			return;
                		}
                        if (rc)
                        {
                            showEditTemplatePage(dto);
                        }
                        else
                        {
                           ClientUtils.showRegisterPassphraseDialog(placeManager,ObidosMessages.LANG.editTemplate(),null);
                        }
                        break;
                    }
                    case copyCol:
                    {
                		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
                		{
                			return;
                		}
                        if (rc)
                        {
                            showCopyTemplatePage(dto);
                        }
                        else
                        {
                           ClientUtils.showRegisterPassphraseDialog(placeManager,ObidosMessages.LANG.editTemplate(),null);
                        }
                        break;
                    }

                    case viewCol:
                    {
                        if (rc)
                        {
                            showViewTemplatePage(dto);
                        }
                        else
                        {
                           ClientUtils.showRegisterPassphraseDialog(placeManager,ObidosMessages.LANG.viewTemplate(),null);
                        }
                        break;
                    }

                    default:
                    {
                        break;
                    }
                }
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        UserService.Utility.getInstance().isPassphraseCached(authCreds,callback);
    }

    @Override
    public void searchTemplateName()
    {
        refreshDataGrid();
    }

	@Override
	public void help()
	{
		showHelp();
	}

	@Override
	protected String getIdName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected BlockQuote getHelpBlockQuote()
	{
		return getView().getHelpBlockQuote();
	}

	@Override
	public void clearselections()
	{
		clearCheckBoxSelections(selectionModel);
		enableButtons(false);
		
	}
    private void promptDeleteTemplates()
    {

		ArrayList<Long> ids = getSelectedIds(selectionModel);
		String s = ClientUtils.getSelectedTypeString(ids, ObidosConstants.TEMPLATE_ID_N);
		String title = glang.deleteGlobalTemplate();
		String message = glang.deleteTemplatesWarning(ids.size(), s, s, s);
        ClientUtils.promptForAction(() -> deleteTemplatesReal(), title, message);
    }

	@Override
	public void deleteTemplates()
	{
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}

		ArrayList<Long> ids = getSelectedIds(selectionModel);
		if (ids == null || ids.size() == 0)
		{
			return;
		}

    	boolean registered = ClientUtils.isPassphraseRegistered(currentUser);
		if (registered)
		{
			promptDeleteTemplates();
		}
		else
		{
			String message = glang.deleteTemplate();
			ClientUtils.showRegisterPassphraseDialog(placeManager, message, null);
		}
	}
	
	private void setOrderBy(OrderBy orderBy)
	{
		sOrderBy = orderBy;
	}
	
	@Override
	public void sortByDate()
	{
		setOrderBy(OrderBy.UPDATE_TIME_DESC);
		refreshDataGrid();
	}

	@Override
	public void sortReverseByDate()
	{
		setOrderBy(OrderBy.UPDATE_TIME_ASC);
		refreshDataGrid();
	}

	@Override
	public void sortByAZ()
	{
		gwtLog("XX Order:");
		setOrderBy(OrderBy.ITEM_NAME_ASC);
		refreshDataGrid();
	}

	@Override
	public void sortByZA()
	{
		setOrderBy(OrderBy.ITEM_NAME_DESC);
		refreshDataGrid();
	}

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
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
