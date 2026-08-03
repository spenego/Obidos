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

package com.spenego.Obidos.client.application.sharecontainerwithusers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.shared.GWT;
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
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.i18n.ObidosMessages;
//import com.spenego.Obidos.client.application.ObidosPresenter.ShareView;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.ContainerService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
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
import com.spenego.Obidos.shared.dto.LicenseStats;
import com.spenego.Obidos.shared.dto.LimitedUserDTO;
import com.spenego.Obidos.shared.dto.LimitedUserResult;
import com.spenego.Obidos.shared.dto.UserDTO;

public class ShareContainerWithUsersPresenter
		extends ObidosPresenter<LimitedUserDTO, ShareContainerWithUsersPresenter.MyView, ShareContainerWithUsersPresenter.MyProxy, ShareContainerWithUsersUiHandlers>
		implements ShareContainerWithUsersUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	// Fix #228
	interface MyView extends com.spenego.Obidos.client.application.ObidosPresenter.ShareView<ShareContainerWithUsersUiHandlers, LimitedUserDTO>
	{
		BlockQuote getHelpBlockQuote();
		TextBox getSerachTextBox();
		ObidosInputGroup getNameInputGroup();
		ObidosMessageRow getMessageRow();
		public Button getShareContainerButton();
		public ToggleSwitch getSendNotificationEmailSwitch();
		public ObidosPanelHeader getPanelHeader();
		public ToggleSwitch getSendNotificationSmsSwitch();
		public Label getLicenseLabel();
		public Label getLicenseLabelSms();
	}


	@Override
	protected String getIdName()
	{
		return ObidosConstants.CONTAINER_ID;
	}

	@NameToken(NameTokens.SHARE_CONTAINER_WITH_USERS)
	@ProxyCodeSplit
	interface MyProxy extends ProxyPlace<ShareContainerWithUsersPresenter>
	{
	}

	@Inject
	ShareContainerWithUsersPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
		// grid.setSelectionModel(selectionModel);
		grid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<LimitedUserDTO> createCheckboxManager());

		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler()
		{

			@Override
			public void onSelectionChange(SelectionChangeEvent event)
			{
				Set<LimitedUserDTO> dtoSet = selectionModel.getSelectedSet();
				if (dtoSet.size() > 0)
				{

					enableButtons(true);
					getView().getSearchTextBox().setValue("");
				} else
				{
					showMessage("");
					enableButtons(false);
				}
			}
		});
		showUserList(selectionModel, grid, this);
        selectCheckBoxByClickingOnTheRow(selectionModel, grid);
	}

	private void enableButtons(boolean enabled)
	{
		getView().getShareContainerButton().setEnabled(enabled);
		getView().getClearButton().setEnabled(enabled);
	}

	private void setShareButtonTitle(String title)
	{
		// don't use setTitle()
		getView().getShareContainerButton().setText(title);

	}

	protected void onReveal()
	{
		super.onReveal();
	}

	protected void onHide()
	{
		gwtLog("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
		super.onHide();
		clearCheckBoxSelections();
	}

	protected void onUnbind()
	{
		super.onUnbind();
	}

	protected void onReset()
	{
		super.onReset();
		checkLicense();
		enableButtons(false);
		updateContainerInfo();
		resetForm();
		refreshDataGrid();
		DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
		ClientUtils.adjustDataGridHeight(grid);
		ClientUtils.addWindowResizeHandler(grid);
		
		grid.setColumnWidth(0, "50px");
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
	private void resetForm()
	{
		showMessage("");
		getView().getShareCommentTextBox().setValue("");
		getView().getSearchTextBox().setValue("");
		getView().getSendNotificationEmailSwitch().setValue(false);
        getView().getSendNotificationSmsSwitch().setValue(false);
        enableButtons(false);
	}

	public void refreshDataGrid()
	{
		DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
		grid.setVisibleRangeAndClearData(grid.getVisibleRange(), true);
	}

	private void updateContainerInfo()
	{
		Long containerId = getId();
		if (containerId == null) {
			return;
		}

		// ContainerDTO get(AuthCredsDTO authCreds, Long containerId) throws
		// ServerSideException;

		GwtAsyncWrapper<ContainerDTO> callback = new GwtAsyncWrapper<ContainerDTO>(this)
		{

			@Override
			public void uponFailure(Throwable caught)
			{
				gwtLog("Could not get Container " + caught.getMessage());
			}

			@Override
			public void uponSuccess(ContainerDTO dto)
			{
				getView().getNameInputGroup().setText(dto.getName());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		ContainerService.Utility.getInstance().get(authCreds, containerId, callback);
	}


	@Override
	public void searchUsersToShare()
	{
		refreshDataGrid();
	}

	private void showUserList(final SelectionModel<LimitedUserDTO> selectionModel,
			final AbstractCellTable<LimitedUserDTO> grid, HasHandlers source)
	{
		grid.setAutoHeaderRefreshDisabled(true);
		grid.setAutoFooterRefreshDisabled(true);

		Code messageLabel = new Code();
		messageLabel.setText("Loading ...");
		grid.setEmptyTableWidget(messageLabel);

		Column<LimitedUserDTO, Boolean> checkColumn = new Column<LimitedUserDTO, Boolean>(new CheckboxCell(true, false))
		{

			@Override
			public Boolean getValue(LimitedUserDTO dto)
			{
            	if (dto.getSelected() == null)
            	{
            		dto.setSelected(false);
            	}
				setSearchedCheckbox(selectionModel, dto);
				return selectionModel.isSelected(dto);
			}

		};
		grid.addColumn(checkColumn, ObidosMessages.LANG.selectLabel());

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
        grid.addColumn(fullnameCol, glang.fullname());


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
        grid.addColumn(emailCol, glang.email());

		AsyncDataProvider<LimitedUserDTO> dataProvider = new AsyncDataProvider<LimitedUserDTO>()
		{

			@Override
			protected void onRangeChanged(HasData<LimitedUserDTO> dto)
			{
				final Range range = dto.getVisibleRange();
				final int start = range.getStart();
				int length = range.getLength();
				GWT.log(" in AsyncDataProvider start: " + start);
				GWT.log("in AsyncDataProvider length: " + length);
				GwtAsyncWrapper<LimitedUserResult> callback = new GwtAsyncWrapper<LimitedUserResult>(source)
				{

					@Override
					public void uponFailure(Throwable caught)
					{
						GWT.log("Exception caught: " + caught.getMessage());
						updateRowCount(0, true);
						messageLabel.setText("");
					}

					@Override
					public void uponSuccess(LimitedUserResult userResult)
					{
						int numberOfUsers = userResult.getTotalUsers();
						GWT.log("Found number of users: " + numberOfUsers);
						List<LimitedUserDTO> users = userResult.getUsers();
						if (users != null && users.size() > 0)
						{
							gwtLog(">>> returned: " + users.size() + " users");
							updateRowCount(numberOfUsers, true);
							updateRowData(start, users);
						} else
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
				gwtLog("Search: " + searchString);
				AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
				UserDTO userDTO = new UserDTO();
				userDTO.setDeleted(false);
                // Allow interact with locked users, Issue # 465
				userDTO.setLocked(null);
				userDTO.setAdministrator(false);
				// Issue # 387
				userDTO.setFullname(searchString);
				final ArrayList<Long> preSelectedUsers = getSelectedIds((MultiSelectionModel<LimitedUserDTO>) selectionModel);
				if (preSelectedUsers != null)
				{
					for (Long psuid : preSelectedUsers)
					{
						gwtLog(" >>> Sending Preselected ID: " + psuid);
					}
				}

				UserService.Utility.getInstance().getUsersForContainer(authCreds, getId(), userDTO, SharedSetQuality.NOT_SHARED_WITH, preSelectedUsers, start, length, toArray(OrderBy.FULLNAME_ASC), callback);
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

	@Override
	public void shareContainer()
	{
		Set<LimitedUserDTO> dtoSet = selectionModel.getSelectedSet();
		if (dtoSet.size() == 0)
		{
			return;
		}

		// works with SingleSelectionModel as well
		/*
		 * Set<LimitedUserDTO> selectedDTOs = selectionModel.getSelectedSet();
		 * GWT.log("Number of rows selected: " + selectedDTOs.size()); for
		 * (LimitedUserDTO dto:selectedDTOs) { GWT.log("Selected: " +
		 * dto.getUsername() + ", " + dto.getFullname()); }
		 */

		Long containerId = getId();
        if (containerId == null) {
        	return;
        }

		GWT.log("Container id: " + containerId);
		String shareComment = getView().getShareCommentTextBox().getValue();
		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not share Container: " + caught.getMessage());
			}

			@Override
			public void uponSuccess(Void result)
			{
				Date date = new Date();
				clearCheckBoxSelections();
				refreshDataGrid();
				if (!dtoSet.isEmpty())
				{
				        String containerName = getView().getNameInputGroup().getText();
					if (dtoSet.size() == 1)
					{
						String fullname = dtoSet.iterator().next().getFullname();
//						showMessage("Container shared with " + fullname + " on " + date.toString());
						showMessage("Container " + "<span style=\"color: purple;\">" +
                                                           "'" +containerName + "'"+ "</span>" + " shared with " +
                                                           "<span style=\"color: purple;\">" + "'" + fullname + "'" + "</span>"+".");
					}
					else
					{
//						showMessage("Container shared with " + dtoSet.size() + " users on " + date.toString());
						showMessage("Container " + "<span style=\"color: purple;\">" +
                                                           "'" +containerName + "'"+ "</span>" + " shared with " +
                                                           "<span style=\"color: purple;\">" + dtoSet.size() + "</span>"+" users.");
					}
				}
				else
				{
					showErrorMessage("No selected users found"); // should not be
				}
				// must clear CheckBox selections or they will accumulate
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		PostOpActions postShareActions = null; 
        if (getView().getSendNotificationEmailSwitch().getValue())
        {
        	gwtLog("Send email about sharing..........");
        	postShareActions = new PostOpActions();
        	postShareActions.setPostOpActions(PostOpActions.SEND_EMAIL);
        }
        else
        {
        	gwtLog("Dont send mail about sharing..");
        }

        if (getView().getSendNotificationSmsSwitch().getValue())
        {
        	gwtLog("Send SMS about sharing ...");
        	if (postShareActions == null)
        	{
        		postShareActions = new PostOpActions();
        	}
        	postShareActions.setPostOpActions(PostOpActions.SEND_SMS);
        }
        else
        {
        	gwtLog("Dont send sms about sharing..");
        }

		ContainerService.Utility.getInstance().shareContainerWithUsers(authCreds, containerId, getIdsFromDTOSet(dtoSet),
				shareComment, postShareActions, callback);
	}

	@Override
	public void listMyContainers()
	{
		ClientUtils.navigateToPlace(placeManager);
	}

	@Override
	public void clearCheckBoxSelections()
	{
		clearCheckBoxSelections(selectionModel);
	}

	private void numberOfUsersSelected()
	{
		Set<LimitedUserDTO> selectedSet = selectionModel.getSelectedSet();
		int n = selectedSet.size();
		if (n > 0)
		{
			showMessage("Number of users selected: " + n);
		}

	}

	@Override
	public void showHideHelp()
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
