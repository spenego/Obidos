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

package com.spenego.Obidos.client.application.listldapsettings;

import java.util.ArrayList;
import java.util.List;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
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
import com.spenego.Obidos.client.application.widgets.bootbox.Bootbox;
import com.spenego.Obidos.client.application.widgets.bootbox.callback.SimpleCallback;
import com.spenego.Obidos.client.application.widgets.bootbox.options.DialogOptions;
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
import com.spenego.Obidos.shared.dto.LdapConfigurationResult;
import com.spenego.Obidos.shared.dto.LimitedLdapDTO;

public class ListLdapSettingsPresenter
        extends Presenter<ListLdapSettingsPresenter.MyView, ListLdapSettingsPresenter.MyProxy>
        implements ListLdapSettingsUiHandlers
{
	ObidosMessages glang = ObidosMessages.LANG;
	
    interface MyView extends View, HasUiHandlers<ListLdapSettingsUiHandlers>
    {
        public DataGrid<LimitedLdapDTO> getDataGrid();
        public SimplePager getPager();
        public ObidosMessageRow getMessageRow();
        public ObidosPanelHeader getPanelHeader();
        public BlockQuote getHelpBlockQuote();
    }


    @NameToken(NameTokens.LIST_LDAP_SETTINGS)
    @ProxyCodeSplit
    @UseGatekeeper(LoggedInAdminGatekeeper.class)
    interface MyProxy extends ProxyPlace<ListLdapSettingsPresenter>
    {
    }

    final PlaceManager placeManager;
    final CurrentUser currentUser;
    @Inject
    ListLdapSettingsPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
        gwtLog("onBind()");
        DataGrid<LimitedLdapDTO> grid = getView().getDataGrid();
        showLdapSettingsList(grid,this);
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
        showMessage(null);
        ClientUtils.setPanelHeaderColor(getView().getPanelHeader(), currentUser);
        refreshDataGrid();
	}

    private void showMessage(String message)
    {
    	getView().getMessageRow().showMessage(message);
    }

    private void showErrorMessage(String errorMessage)
    {
    	getView().getMessageRow().showErrorMessage(errorMessage);
    }

    public void refreshDataGrid()
    {
        DataGrid<LimitedLdapDTO> grid = getView().getDataGrid();
        Range range = new Range(0,ObidosConstants.VISIBLE_GRID_COUNT);
        grid.setVisibleRangeAndClearData(range,true);
    }

    private void showLdapSettingsList(final AbstractCellTable<LimitedLdapDTO> grid, HasHandlers source)
    {
        int h = Window.getClientHeight();
        double hh = h * 0.80;
        h = (int) hh;
        String hs = Integer.toString(h) + "px";
        grid.setHeight(hs);

        grid.setAutoHeaderRefreshDisabled(true);
        grid.setAutoFooterRefreshDisabled(true);

        Code messageLabel = new Code();
        messageLabel.setText("Loading ...");
        grid.setEmptyTableWidget(messageLabel);

        TextColumn<LimitedLdapDTO> configNameColumn = new TextColumn<LimitedLdapDTO>()
        {
            @Override
            public String getValue(LimitedLdapDTO ldapDTO)
            {
                return ldapDTO.getName();
            }
        };
        grid.addColumn(configNameColumn, "AD/LDAP Config name");

        TextColumn<LimitedLdapDTO> uriNameColumn = new TextColumn<LimitedLdapDTO>()
        {
            @Override
            public String getValue(LimitedLdapDTO ldapDTO)
            {
                return ldapDTO.getLdapuri();
            }
        };
        grid.addColumn(uriNameColumn, "LDAP URI");

        // Handler is at the bottom

         // Delete
         ObidosButtonCell delCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_DELETE);
        final Column<LimitedLdapDTO, String> delCol = new Column<LimitedLdapDTO, String> (delCell)
        {

                    @Override
                    public String getValue(LimitedLdapDTO dto)
                    {
                        return glang.deleteButtonTitle();
                    }

         };
         grid.addColumn(delCol, glang.deleteButtonTitle());

         
         // Edit
         ObidosButtonCell editCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_EDIT);
        final Column<LimitedLdapDTO, String> editCol = new Column<LimitedLdapDTO, String> (editCell)
		{
			@Override
			public String getValue(LimitedLdapDTO object)
			{
				return glang.editButtonTitle();
			}
	
		};
		grid.addColumn(editCol, glang.editButtonTitle());

        AsyncDataProvider<LimitedLdapDTO> dataProvider = new AsyncDataProvider<LimitedLdapDTO>()
        {

            @Override
            protected void onRangeChanged(HasData<LimitedLdapDTO> ldapDTO)
            {
                final Range range = ldapDTO.getVisibleRange();
                final int start = range.getStart();
                int length = range.getLength();
                showMessage("");
                //Log.info(() -> " in AsyncDataProvider start: " + start);
                //Log.info(() -> "in AsyncDataProvider length: " + length);

                GwtAsyncWrapper<LdapConfigurationResult> callback = new GwtAsyncWrapper<LdapConfigurationResult>(source)
                {

                    @Override
                    public void uponFailure(Throwable e)
                    {
                        showErrorMessage("Error:" + e.getMessage());
                    }

                    @Override
                    public void uponSuccess(LdapConfigurationResult result)
                    {
                        int total = result.getTotal();
                        List<LimitedLdapDTO> settings = result.getElements();
                        if (settings != null && !settings.isEmpty())
                        {
                            updateRowData(start, settings);
                            updateRowCount(total, true);
                        }
                        else
                        {
                            messageLabel.setText("No LDAP Settings found");
                            updateRowCount(0, true);
                        }
                    }
                };
                AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
                AdminConfigService.Utility.getInstance().getAllLdapSettings(authCreds, null, start, length, callback);

            }
        };

        // handler for Edit button
        editCol.setFieldUpdater(new FieldUpdater<LimitedLdapDTO, String>()
        {

            @Override
            public void update(int idx, LimitedLdapDTO ldapDTO, String value)
            {
                showEditLdapSettingsPage(ldapDTO);
            }
         });

        // handler for Delete button
        delCol.setFieldUpdater(new FieldUpdater<LimitedLdapDTO, String>()
        {

            @Override
            public void update(int idx, LimitedLdapDTO ldapDTO, String value)
            {
                promptDelete(grid, idx, ldapDTO);
                grid.redraw();
            }

         });

        getView().getPager().setDisplay(grid);
        dataProvider.addDataDisplay(grid);
    }

    private void promptDelete(final AbstractCellTable<LimitedLdapDTO> grid, int idx, LimitedLdapDTO ldapDTO)
    {
        String message = "Delete AD/LDAP configuration?";
        String title = null;

        DialogOptions options = DialogOptions.newOptions(message);
        options.setTitle(title);
        options.setOnEscape(new SimpleCallback()
        {

            @Override
            public void callback()
            {
            }
        });

       // No
       options.addButton("No", ButtonType.DEFAULT.getCssName(), new SimpleCallback()
       {
            @Override
            public void callback()
            {
            }
       });

        // Yes
        options.addButton("Yes", ButtonType.DANGER.getCssName(), new SimpleCallback()
        {

            @Override
            public void callback()
            {
                deleteLdapConfig(grid, ldapDTO);
            }
        });
        Bootbox.dialog(options);
    }

    private void deleteLdapConfig(final AbstractCellTable<LimitedLdapDTO> grid, LimitedLdapDTO ldapDTO)
    {
		final ArrayList<Long> ldapIds = new ArrayList<>(1);
		ldapIds.add(ldapDTO.getId());
        GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
        {

            @Override
            public void uponFailure(Throwable e)
            {
                ClientUtils.showBootboxDialog("ERROR Deleting LDAP configuration", "Could not delete configuration: " + ldapDTO.getName());
            }

            @Override
            public void uponSuccess(Void rc)
            {
                grid.setVisibleRangeAndClearData(grid.getVisibleRange(),true);
            }
        };
        AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
        AdminConfigService.Utility.getInstance().deleteLdapConfig(authCreds, ldapIds, callback);
    }

    private void showEditLdapSettingsPage(LimitedLdapDTO dto)
    {
        PlaceRequest placeRequest = new PlaceRequest.Builder()
            .nameToken(NameTokens.LDAPSETTINGS)
            .with(ObidosConstants.ACTION,ObidosConstants.EDIT)
            .with(ObidosConstants.LDAP_ID,dto.getId().toString())
            .build();
        placeManager.revealPlace(placeRequest);
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


	@Override
	public void goback()
	{
		ClientUtils.goBack(placeManager);
	}
}
