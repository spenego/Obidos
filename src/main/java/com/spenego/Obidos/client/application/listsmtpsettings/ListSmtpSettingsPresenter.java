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

package com.spenego.Obidos.client.application.listsmtpsettings;

import java.util.ArrayList;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
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
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.AdminConfigService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInAdminGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.SmtpConfigDTO;

public class ListSmtpSettingsPresenter extends Presenter<ListSmtpSettingsPresenter.MyView, ListSmtpSettingsPresenter.MyProxy>
        implements ListSmtpSettingsUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
    interface MyView extends View, HasUiHandlers<ListSmtpSettingsUiHandlers>
    {
        public DataGrid<SmtpConfigDTO> getDataGrid();
        public SimplePager getPager();
        public BlockQuote getHelpBlockQuote();
        public ObidosPanelHeader getPanelHeader();
        public ObidosMessageRow getMessageRow();
    }

    @NameToken(NameTokens.LIST_SMTP_SETTINGS)
    @ProxyCodeSplit
    @UseGatekeeper(LoggedInAdminGatekeeper.class)
    interface MyProxy extends ProxyPlace<ListSmtpSettingsPresenter>
    {
    }

    private final PlaceManager placeManager;
    private final CurrentUser currentUser;

    @Inject
    ListSmtpSettingsPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
        gwtLog(">>>>>>>>>>>>>>>>> onBind()");
        DataGrid<SmtpConfigDTO> grid = getView().getDataGrid();
        showDefaultSmtpServer(grid, this);
    }

    protected void onReveal()
    {
        super.onReveal();
        gwtLog("onReveal()");
    }

    protected void onHide()
    {
        super.onHide();
        gwtLog("onHide()");
    }

    protected void onUnbind()
    {
        super.onUnbind();
        gwtLog("onUnbind()");

    }

    protected void onReset()
    {
        super.onReset();
        gwtLog(">> onReset() refreshing datagrid");
        gwtLog("is admin: " + currentUser.getUserDTO().getAdministrator());
        if (!ClientUtils.isAdmin(currentUser))
        {
            gwtLog("will not call rpc, not an admin");
            return;

        }
        refreshDataGrid();
        DataGrid<SmtpConfigDTO> grid = getView().getDataGrid();
        ClientUtils.adjustDataGridHeight(grid);
        ClientUtils.addWindowResizeHandler(grid);
    }

    public void refreshDataGrid()
    {
        DataGrid<SmtpConfigDTO> grid = getView().getDataGrid();
        Range range = new Range(0,ObidosConstants.VISIBLE_GRID_COUNT);
        grid.setVisibleRangeAndClearData(range,true);
    }

    @Override
    public void editSmtpServer(SmtpConfigDTO smtpConfigDTO)
    {
        PlaceRequest placeRequest = new PlaceRequest.Builder()
                .nameToken(NameTokens.SMTPSETTINGS)
                .with(ObidosConstants.ACTION,"edit")
                .with("name",smtpConfigDTO.getName())
                .build();
        placeManager.revealPlace(placeRequest);
    }

    private void showDefaultSmtpServer(final DataGrid<SmtpConfigDTO> grid, HasHandlers source)
    {
        grid.setAutoHeaderRefreshDisabled(true);
        grid.setAutoFooterRefreshDisabled(true);

	    Code messageLabel = new Code();
	    messageLabel.setText(glang.loading());
	    grid.setEmptyTableWidget(messageLabel);

        TextColumn<SmtpConfigDTO> configNameColumn = new TextColumn<SmtpConfigDTO>()
        {
            @Override
            public String getValue(SmtpConfigDTO smtpDTO)
            {
                return smtpDTO.getSmtpServer();
            }
        };
        grid.addColumn(configNameColumn, "SMTP Server");


        // Delete button
        // Handler is at the bottom
        ObidosButtonCell obc = new ObidosButtonCell(ObidosConstants.CELL_TYPE_DELETE);
        final Column<SmtpConfigDTO, String> delCol = new Column<SmtpConfigDTO, String> (obc)
        {

                    @Override
                    public String getValue(SmtpConfigDTO dto)
                    {
                        return ObidosMessages.LANG.deleteButtonTitle();
                    }

         };
         grid.addColumn(delCol, ObidosMessages.LANG.deleteButtonTitle());

        // Edit button
        // Handler is at the bottom
        obc = new ObidosButtonCell(ObidosConstants.CELL_TYPE_EDIT);
        final Column<SmtpConfigDTO, String> editCol = new Column<SmtpConfigDTO, String> (obc)
        {

                    @Override
                    public String getValue(SmtpConfigDTO dto)
                    {
                        return ObidosMessages.LANG.editButtonTitle();
                    }

         };
         grid.addColumn(editCol,ObidosMessages.LANG.editButtonTitle());

        AsyncDataProvider<SmtpConfigDTO> dataProvider = new AsyncDataProvider<SmtpConfigDTO>()
        {

            @Override
            protected void onRangeChanged(HasData<SmtpConfigDTO> smtpDTO)
            {
//                final Range range = smtpDTO.getVisibleRange();
//                final int start = range.getStart();
//                int length = range.getLength();
                final int start = 0;
                //int length = 1;
//                showMessage("");

                GwtAsyncWrapper<SmtpConfigDTO> callback = new GwtAsyncWrapper<SmtpConfigDTO>(source)
                {

                    @Override
                    public void uponFailure(Throwable e)
                    {
                        updateRowCount(0, true);
                        String ex = e.getMessage();
                        String msg = "Could not find any SMTP settings";
                        if (ex != null)
                        {
                        	msg = msg + ": " + ex;
                        	
                        }
                        messageLabel.setText(msg);
                    }

                    @Override
                    public void uponSuccess(SmtpConfigDTO dto)
                    {
                        gwtLog("GOT smtp server: " + dto.getSmtpServer());
                        int total = 1;
                        ArrayList<SmtpConfigDTO> settings = new ArrayList<>();
                        settings.add(dto);

                        if (settings != null && settings.size() > 0)
                        {
//                            listMessageLabel.setText("Default SMTP Server");
                            updateRowData(start, settings);
                            updateRowCount(total, true);
                        }
                        else
                        {
                            messageLabel.setText("No SMTP Settings found");
                            updateRowCount(0, true);
                        }
                    }
                };
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                String name="default";
                AdminConfigService.Utility.getInstance().getSmtpConfig(authCreds, name, callback);
            }
        };

        // handler for Delete button
        delCol.setFieldUpdater(new FieldUpdater<SmtpConfigDTO, String>()
        {

            @Override
            public void update(int idx, SmtpConfigDTO smtpDTO, String value)
            {
                promptDelete(smtpDTO);
            }

         });

        // handler for Edit button
        editCol.setFieldUpdater(new FieldUpdater<SmtpConfigDTO, String>()
        {

            @Override
            public void update(int idx, SmtpConfigDTO smtpDTO, String value)
            {
                editSmtpServer(smtpDTO);
            }

         });

        getView().getPager().setDisplay(grid);
        dataProvider.addDataDisplay(grid);
    }

    private void promptDelete(SmtpConfigDTO smtpDTO)
    {
    	ObidosMessages lang = ObidosMessages.LANG;
        String title = lang.warning();
        String message = lang.smtpSettingsDeleteWarning(smtpDTO.getName());
		ClientUtils.promptForAction(() -> deleteSmtpConfig(smtpDTO), title, message);
    }

    private void deleteSmtpConfig(SmtpConfigDTO dto)
    {
    	if (dto == null) {
    		return;
    	}
        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {
            @Override
            public void uponFailure(Throwable e)
            {
                String errorMessage = "Could not delete configuration: " + e.getMessage();
                showErrorMessage(errorMessage);
            }

            @Override
            public void uponSuccess(Void rc)
            {
            	showMessage("SMTP configuration deleted");
            	refreshDataGrid();
            }
        };
        AuthCredsDTO authCredsDTO = ClientUtils.getAuthCreds();
        //Boolean deletePermanently = Boolean.TRUE;
        // Issue #420 TODO
        // we only have 1 setting right now
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(dto.getId());

        AdminConfigService.Utility.getInstance().deleteSmtpConfig(authCredsDTO, ids, callback);
    }
    
    private void showMessage(String message)
    {
    	getView().getMessageRow().showMessage(message);
    }
    
    private void showErrorMessage(String errorMessage)
    {
    	getView().getMessageRow().showErrorMessage(errorMessage);
    }

    private void gwtLog(String message)
    {
        ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
    }

	@Override
	public void help()
	{
		ClientUtils.showHelp(getView().getHelpBlockQuote());
	}
}
