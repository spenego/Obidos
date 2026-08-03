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

package com.spenego.Obidos.client.application.listtemplates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
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
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.TemplateService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInUserLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeResult;
import com.spenego.Obidos.shared.exceptions.ObidosException;

public class ListTemplatesPresenter
        extends ObidosPresenter<UserDefinedTypeDTO, ListTemplatesPresenter.MyView, ListTemplatesPresenter.MyProxy, ListTemplatesUiHandlers>
        implements ListTemplatesUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
    private enum FieldNumber
    {
        editCol,
        copyCol,
        viewCol
    };

    interface MyView extends View, HasUiHandlers<ListTemplatesUiHandlers>
    {
        public DataGrid<UserDefinedTypeDTO> getDataGrid();
        public SimplePager getPager();
        public TextBox getSearchTextBox();
        public Button getSearchButton();
        public Button getHelpButton();
        public BlockQuote getHelpBlockQuote();
        public ObidosMessageRow getMessageRow();
        public ObidosPanelHeader getPanelHeader();
        public Button getDeleteButton();
        public ObidosClearSelectionsButton getClearButton();
       	public ListBox getLanguageListBox();
       	public ObidosRowBottom2px getLanguageRow();
    }

	@Override
	protected String getIdName() {
		return ObidosConstants.TEMPLATE_TYPE;
	}

    @NameToken(NameTokens.LIST_TEMPLATES)
    @ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
    interface MyProxy extends ProxyPlace<ListTemplatesPresenter>
    {
    }

    @Inject
    ListTemplatesPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            CurrentUser currentUser)
    {
        super(eventBus, view, proxy, placeManager, currentUser);

        getView().setUiHandlers(this);
    }

    protected void onBind()
    {
        super.onBind();
        gwtLog("onBind()");
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
        showMessage(null);
        clearselections();
        DataGrid<UserDefinedTypeDTO> grid = getView().getDataGrid();
        updateForms();
        refreshDataGrid(grid);
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.adjustDataGridHeight(grid);
        setColumnWidth(grid, 0, "55px");  // Checkbox column
        getView().getClearButton().setEnabled(false);
		ClientUtils.showLanguageListBox(getView().getLanguageRow(), getView().getLanguageListBox());
    }
    
    private void updateForms()
    {
        int templateType = ClientUtils.getTemplateTypeFromUrl(placeManager);
        switch(templateType)
        {
            case ObidosConstants.TEMPLATE_TYPE_PERSONAL:
            {
                updatePanelTitle(ObidosMessages.LANG.personalTemplate());
                getView().getSearchTextBox().setPlaceholder(ObidosMessages.LANG.searchPersonalTemplateName());
                break;
            }

            case ObidosConstants.TEMPLATE_TYPE_GLOBAL:
            {
                updatePanelTitle(ObidosMessages.LANG.globalTemplate());
                getView().getSearchTextBox().setPlaceholder(ObidosMessages.LANG.searchGlobalTemplateName());
                break;
            }
            default:
            {
                showErrorMessage("Could not get " + ObidosConstants.TEMPLATE_TYPE + " from URL");
                updatePanelTitle("Unknown Template Type");
                return;
            }
        }
    }

    private void enableButtons(boolean enabled)
    {
    	getView().getDeleteButton().setEnabled(enabled);
    	getView().getClearButton().setEnabled(enabled);
    }


    public void refreshDataGrid(DataGrid<UserDefinedTypeDTO> grid)
    {
        grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
    }

    // TODO: This should be moved to ObidosPresenter
    private <T,C> void setColumnUpdater(Column<T, C> col, final Consumer<T> consumer, final Supplier<C> passphraseDialogMessage) {
           col.setFieldUpdater(new FieldUpdater<T, C>() {
            @Override
            public void update(int idx, T dto, C value) {
            	boolean rc = ClientUtils.isPassphraseRegistered(currentUser);
            	currentUser.setPassphraseRegistered(rc);
                if (rc) {
                    gwtLog("Passphrase is registered");
                	consumer.accept(dto);
                } else {
                	ClientUtils.showRegisterPassphraseDialog(placeManager, passphraseDialogMessage.get().toString(), null);
                }
            }});
    }
    
    private ButtonCell copyButtonCell()
    {
    	ButtonCell bCell = new ButtonCell(ButtonType.LINK, IconType.COPY);
    	return bCell;
    }

    private void showTypeTemplates(final SelectionModel<UserDefinedTypeDTO> selectionModel,
    		final AbstractCellTable<UserDefinedTypeDTO> grid, HasHandlers source)
    {
    	ObidosMessages lang = ObidosMessages.LANG;
        grid.setAutoHeaderRefreshDisabled(true);
        grid.setAutoFooterRefreshDisabled(true);

//        Label messageLabel = new Label();
//        messageLabel.setType(LabelType.INFO);
//        messageLabel.setText("Loading ...");
//        grid.setEmptyTableWidget(messageLabel);
        Code messageLabel = new Code();
        messageLabel.setText("Loading ...");
        grid.setEmptyTableWidget(messageLabel);

        // Select CheckBox
        addCheckBoxColumn(grid, dto -> selectionModelValue(selectionModel, dto));
        
        // name
        ObidosButtonCell nameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_TEMPLATE_NAME);
        final Column<UserDefinedTypeDTO, String> nameCol = new Column<UserDefinedTypeDTO, String>(nameCell)
        {

            @Override
            public String getValue(UserDefinedTypeDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(nameCol, lang.templateNameLabel());
        
        Column<UserDefinedTypeDTO, String> editCol  = addObidosButtonCellColumn(grid, dto -> lang.editButtonTitle(), lang.editTemplate(),  ObidosConstants.CELL_TYPE_EDIT);
        Column<UserDefinedTypeDTO, String> copyCol  = addObidosButtonCellColumn(grid, dto -> lang.copyButtonTitle(), lang.copyTemplateTitle(),  ObidosConstants.CELL_TYPE_COPY);

        setColumnUpdater(nameCol, dto -> showViewTemplatePage(dto),  () -> lang.viewTemplate());
        setColumnUpdater(editCol, dto -> showEditTemplatePage(dto),  () -> lang.editTemplate());
        setColumnUpdater(copyCol, dto -> showCopyTemplatePage(dto),  () -> lang.copyTemplateTitle());


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
                            messageLabel.setText("No Personal Templates found");
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
                String search = getView().getSearchTextBox().getValue();
                if (search == null || search.length() == 0)
                {
                	search = null;
                }

                Long userId;
                try
                {
                    userId = ClientUtils.getLoggedInUserId(currentUser);
                } catch (ObidosException e)
                {
                    showErrorMessage("Could not find logged in user id");
                    return;
                }
                gwtLog(">>>>>>>>>>>>>> userid: " + userId);
                boolean personal = true;
                int templateType = ClientUtils.getTemplateTypeFromUrl(placeManager);
                switch(templateType)
                {
                    case ObidosConstants.TEMPLATE_TYPE_PERSONAL:
                    {
                        personal = true;
                        break;
                    }

                    case ObidosConstants.TEMPLATE_TYPE_GLOBAL:
                    {
                        userId = null;
                        personal = false;
                        break;
                    }
                    default:
                    {
                        showErrorMessage("Could not get " + ObidosConstants.TEMPLATE_TYPE + " from URL");
                        return;
                    }
                }
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                List<Long> preSelectedTemplates = null;
                TemplateService.Utility.getInstance().getUserDefinedTypes(authCreds, personal, userId, search, preSelectedTemplates, start, length, getOrderByList(), callback);
            }
        };

        getView().getPager().setDisplay(grid);
        dataProvider.addDataDisplay(grid);

    }

    private void updatePanelTitle(String title)
    {
        getView().getPanelHeader().setHeadingText(title);
    }

    private void showCopyTemplatePage(UserDefinedTypeDTO dto)
    {
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}
        String nameToken = NameTokens.COPY_TEMPLATE;
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.TEMPLATE_ID, dto.getId().toString());
        with.put(ObidosConstants.TEMPLATE_TYPE,ObidosConstants.PERSONAL);
        ClientUtils.showPage(placeManager, nameToken, with);
    }
    
    private void showViewTemplatePage(UserDefinedTypeDTO dto)
    {
        String templateType = ObidosConstants.PERSONAL;
        String nameToken = NameTokens.EDIT_TEMPLATE;
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.TEMPLATE_ID, dto.getId().toString());
        with.put(ObidosConstants.ACTION, ObidosConstants.VIEW);
        with.put(ObidosConstants.TEMPLATE_TYPE, templateType);
        ClientUtils.showPage(placeManager, nameToken, with);

    }


    private void showEditTemplatePage(UserDefinedTypeDTO dto)
    {
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}
        gwtLog("Show edit template page, template id: " + dto.getId());
        String templateType = ObidosConstants.PERSONAL;
        String nameToken = NameTokens.EDIT_TEMPLATE;
        Map<String,String> with = new HashMap<>();
        with.put(ObidosConstants.TEMPLATE_ID, dto.getId().toString());
        with.put(ObidosConstants.ACTION, ObidosConstants.EDIT);
        with.put(ObidosConstants.TEMPLATE_TYPE, templateType);
        ClientUtils.showPage(placeManager, nameToken, with);
    }

	@Override
	public void searchTemplateName()
	{
		refreshDataGrid(getView().getDataGrid());
	}

	@Override
	public void help()
	{
		showHelp();
	}

	@Override
	protected BlockQuote getHelpBlockQuote() {
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
		String title = glang.deletePersonalTemplate();
		String message = glang.deleteTemplatesWarning(ids.size(), s, s, s);
        ClientUtils.promptForAction(() -> deleteTemplatesReal(), title, message);
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
            	clearselections();
            	refreshDataGrid(getView().getDataGrid());
            }

            @Override
            public void uponFailure(Throwable caught)
            {
                showErrorMessage(ObidosMessages.LANG.couldNotDeleteTemplate() + ": " + caught.getMessage());
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        TemplateService.Utility.getInstance().deleteUserDefinedType(authCreds, ids, callback);


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
       	refreshDataGrid(getView().getDataGrid());
	}

	@Override
	public void sortReverseByDate()
	{
		setOrderBy(OrderBy.UPDATE_TIME_ASC);
       	refreshDataGrid(getView().getDataGrid());
	}

	@Override
	public void sortByAZ()
	{
		setOrderBy(OrderBy.ITEM_NAME_ASC);
       	refreshDataGrid(getView().getDataGrid());
	}

	@Override
	public void sortByZA()
	{
		setOrderBy(OrderBy.ITEM_NAME_DESC);
       	refreshDataGrid(getView().getDataGrid());
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
