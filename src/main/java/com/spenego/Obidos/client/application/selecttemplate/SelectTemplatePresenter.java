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

package com.spenego.Obidos.client.application.selecttemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.IconPosition;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ObidosPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.TemplateService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeResult;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

public class SelectTemplatePresenter extends ObidosPresenter<UserDefinedTypeDTO, SelectTemplatePresenter.MyView, SelectTemplatePresenter.MyProxy, SelectTemplateUiHandlers>
        implements SelectTemplateUiHandlers
{
	ObidosMessages glang = ObidosMessages.LANG;
    final SingleSelectionModel<UserDefinedTypeDTO> selectionModel = new SingleSelectionModel<UserDefinedTypeDTO>();
    interface MyView extends View, HasUiHandlers<SelectTemplateUiHandlers>
    {
        public DataGrid<UserDefinedTypeDTO> getDataGrid();
        public SimplePager getPager();
        public ObidosPanelHeader getPanelHeader();
        public TextBox getSerachTextBox();
        public Button getSelectButton();
        public Button getSearchButton();
        public BlockQuote getHelpBlockQuote();
        public ObidosMessageRow getMessageRow();
        public ObidosButtonToolBar getButtonToolBar();
    }

	@Override
	protected String getIdName() {
		return ObidosConstants.TEMPLATE_TYPE;
	}

    @NameToken(NameTokens.SELECT_TEMPLATE)
    @ProxyCodeSplit
    interface MyProxy extends ProxyPlace<SelectTemplatePresenter>
    {
    }

    @Inject
    SelectTemplatePresenter(EventBus eventBus, MyView view, MyProxy proxy,
            final PlaceManager placeManager,
            final CurrentUser currentUser)
    {
        super(eventBus, view, proxy, placeManager, currentUser);

        getView().setUiHandlers(this);
    }

    protected void onBind()
    {
        super.onBind();
        DataGrid<UserDefinedTypeDTO> grid = getView().getDataGrid();
        grid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
        grid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<UserDefinedTypeDTO> createCheckboxManager());
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler()
        {

            @Override
            public void onSelectionChange(SelectionChangeEvent event)
            {
                UserDefinedTypeDTO dto = selectionModel.getSelectedObject();
               if (dto != null)
               {
                   gwtLog("Selected: " + dto.getName());
                   enableDisableAddButton(true);
                   setSubmitButtonTitle(ObidosMessages.LANG.next());
                   setSubmitButtonIcon(IconType.ARROW_RIGHT);
                   setSubmitButtonIconPosition(IconPosition.RIGHT);
               }
               else
               {
                   gwtLog("Unselected");
                   enableDisableAddButton(false);
                   setSubmitButtonTitle(glang.selectTemplate());
                   setSubmitButtonIcon(null);
               }
            }
        });
        showTemplates(selectionModel,grid, this);
        selectCheckBoxByClickingOnTheRow(selectionModel,grid);
    }

    protected void onReveal()
    {
        super.onReveal();
    }

    protected void onHide()
    {
        super.onHide();
	    setSubmitButtonTitle(glang.selectTemplate());
	    setSubmitButtonIcon(null);
    }

    protected void onUnbind()
    {
        super.onUnbind();
    }

    protected void onReset()
    {
        super.onReset();
        showMessage("");
        getView().getButtonToolBar().adjustButtonsWidth();
        enableDisableAddButton(false);
//        ClientUtils.updateListButtonTitle(placeManager, getView().getListButton());
        refreshDataGrid();
        DataGrid<UserDefinedTypeDTO> grid = getView().getDataGrid();
        setColumnWidth(grid, 0, "55px");  // Checkbox column
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
    }

    void enableDisableAddButton(boolean enabled)
    {
        getView().getSelectButton().setEnabled(enabled);
    }

    private void showTemplates(final SelectionModel<UserDefinedTypeDTO> selectionModel, final AbstractCellTable<UserDefinedTypeDTO> grid, HasHandlers source)
    {
        grid.setAutoHeaderRefreshDisabled(true);
        grid.setAutoFooterRefreshDisabled(true);

        Code messageLabel = new Code();
        messageLabel.setText("Loading ...");
        grid.setEmptyTableWidget(messageLabel);

        Column<UserDefinedTypeDTO,Boolean> checkColumn =
                new Column<UserDefinedTypeDTO, Boolean>(new CheckboxCell(true,false))
        {

            @Override
            public Boolean getValue(UserDefinedTypeDTO dto)
            {
                return selectionModel.isSelected(dto);
            }
        };
        grid.addColumn(checkColumn, glang.select());

        // Personal Template Name
        final TextColumn<UserDefinedTypeDTO> groupNameColumn = new TextColumn<UserDefinedTypeDTO>()
        {

            @Override
            public String getValue(UserDefinedTypeDTO dto)
            {
                if (dto != null)
                    return dto.getName();
                else
                    return("N/A");
            }
        };
        grid.addColumn(groupNameColumn, "Template Name");

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
                        String message = t.getMessage();
                        showMessage("Could not fetch any templates" + message);
                    }

                    @Override
                    public void uponSuccess(UserDefinedTypeResult result)
                    {
                        int n = result.getTotalTypes();
                        gwtLog("Number of templates: " + n);
                        if (n == 0)
                        {
                            messageLabel.setText("Could not fetch any Templates");
                            updateRowCount(0, true);
                            return;
                        }
                        List<UserDefinedTypeDTO> list = result.getTypes();
                        if (list != null && list.size() > 0)
                        {
                            updateRowCount(n, true);
                            updateRowData(start, list);
                        }
                        else
                        {
                            messageLabel.setText("No Templates found");
                            updateRowCount(0, true);
                        }
                    }
                };
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                String search = null;
                Long userId;
                try
                {
                    userId = ClientUtils.getLoggedInUserId(currentUser);
                } catch (NumberFormatException e)
                {
                    showErrorMessage("Could not find logged in user id");
                    return;
                }
                search = getView().getSerachTextBox().getValue();
                if (search == null || search.length() == 0)
                {
                    search = null;
                }
                boolean personal = true;
                int templateType = ClientUtils.getTemplateTypeFromUrl(placeManager);
                getView().getSelectButton().setText(ObidosMessages.LANG.selectTemplate());
                switch(templateType)
                {
                    case ObidosConstants.TEMPLATE_TYPE_PERSONAL:
                    {
                        personal = true;
                        getView().getPanelHeader().setHeadingText(ObidosMessages.LANG.selectPersonalTemplate());
                        break;
                    }

                    case ObidosConstants.TEMPLATE_TYPE_GLOBAL:
                    {
                        userId = null;
                        personal = false;
                        getView().getPanelHeader().setHeadingText(ObidosMessages.LANG.selectGlobalTemplate());
                        break;
                    }
                    default:
                    {
                        showErrorMessage("Could not get " + ObidosConstants.TEMPLATE_TYPE + " from URL");
                        return;
                    }
                }
                List<Long> preSelectedTemplates = null;
//                TemplateService.Utility.getInstance().getUserDefinedTypes(authCreds, personal, userId, search, preSelectedTemplates, start, length, toArray(OrderBy.ITEM_NAME_ASC), callback);
                TemplateService.Utility.getInstance().getUserDefinedTypes(authCreds, personal, userId, search, preSelectedTemplates, start, length, getOrderByList(), callback);
            }
        };

        getView().getPager().setDisplay(grid);
        dataProvider.addDataDisplay(grid);
    }

    private void setSubmitButtonTitle(String title)
    {
        getView().getSelectButton().setText(title);

    }

    private void setSubmitButtonIcon(IconType icon)
    {
        getView().getSelectButton().setIcon(icon);

    }

    private void setSubmitButtonIconPosition(IconPosition position)
    {
        getView().getSelectButton().setIconPosition(position);

    }

    public void refreshDataGrid()
    {
        DataGrid<UserDefinedTypeDTO> grid = getView().getDataGrid();
        Range range = new Range(0,ObidosConstants.VISIBLE_GRID_COUNT);
        grid.setVisibleRangeAndClearData(range,true);
    }

    @Override
    public void searchTemplate()
    {
        refreshDataGrid();
    }

    @Override
    public void navigateToAddItemToContainerView()
    {
        UserDefinedTypeDTO dto = selectionModel.getSelectedObject();
        if (dto == null)
        {
            return;
        }
        String containerId = null;
        String templateType = null;
        // Bug #148, owner id was not passed to the next screen
        // which prevented to fetch the container and populate 
        // the form
        // Jan-21-2026
        String ownerId = null;
        String key = ObidosConstants.CONTAINER_ID;
        try
        {
            containerId = ClientUtils.getParameterFromUrl(placeManager, key);
        } catch (ParamNotFoundException e)
        {
            showErrorMessage("Could not get " + key + " from URL");
            return;
        }
        key = ObidosConstants.OWNERID;
        try
        {
            ownerId = ClientUtils.getParameterFromUrl(placeManager, key);
        } catch (ParamNotFoundException e)
        {
            //showErrorMessage("Could not get " + key + " from URL");
            //return;
        }

        key = ObidosConstants.TEMPLATE_TYPE;
        try
        {
            templateType = ClientUtils.getParameterFromUrl(placeManager, key);
        } catch (ParamNotFoundException e)
        {
            showErrorMessage("Could not get template " + key + " from URL");
            return;
        }
        


        Map<String,String> with = new HashMap<>();
        key = ObidosConstants.CONTAINER_ID;
        with.put(key, containerId);

        key = ObidosConstants.TEMPLATE_TYPE;
        with.put(key,  templateType);

        key = ObidosConstants.TEMPLATE_ID;
        with.put(key, dto.getId().toString());

        if (ownerId != null)
        {
            key = ObidosConstants.OWNERID;
            with.put(key, ownerId);
        }
        
       	String place = ClientUtils.getPlaceFromUrl(placeManager);
        if (place != null)
        {
        	key = ObidosConstants.PLACE;
        	with.put(key,  place);
        }

        String nameToken = NameTokens.ADD_ITEM_TO_CONTAINER;

        ClientUtils.showPage(placeManager, nameToken, with);
    }

    @Override
    public void navigateToListMyContainersView()
    {
    	ClientUtils.showListContainersPage(placeManager);
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
		setOrderBy(OrderBy.ITEM_NAME_ASC);
		refreshDataGrid();
	}

	@Override
	public void sortByZA()
	{
		setOrderBy(OrderBy.ITEM_NAME_DESC);
		refreshDataGrid();
	}

}
