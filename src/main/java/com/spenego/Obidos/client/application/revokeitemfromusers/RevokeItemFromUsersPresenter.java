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

package com.spenego.Obidos.client.application.revokeitemfromusers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.Range;
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
import com.spenego.Obidos.client.place.NameTokens;
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
import com.spenego.Obidos.shared.dto.ItemDTO;
import com.spenego.Obidos.shared.dto.LimitedUserDTO;
import com.spenego.Obidos.shared.dto.LimitedUserResult;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeValueDTO;

public class RevokeItemFromUsersPresenter
		extends ObidosPresenter<LimitedUserDTO, RevokeItemFromUsersPresenter.MyView, RevokeItemFromUsersPresenter.MyProxy, RevokeItemFromUsersUiHandlers>
		implements RevokeItemFromUsersUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;

	interface MyView extends com.spenego.Obidos.client.application.ObidosPresenter.RevokeView<RevokeItemFromUsersUiHandlers,LimitedUserDTO>
	{
		public Row getContainerRow();
		public FormLabel getItemNameLabel();
		public ObidosInputGroup getContainerNameInputGroup();
		public ObidosInputGroup getItemNameInputGroup();
		public BlockQuote getHelpBlockQuote();
		public ObidosMessageRow getMessageRow();
		public ObidosPanelHeader getPanelHeader();
		public ToggleSwitch getSendNotificationEmailSwitch();
	}

	@NameToken(NameTokens.REVOKE_ITEM_FROM_USERS)
	@ProxyCodeSplit
	interface MyProxy extends ProxyPlace<RevokeItemFromUsersPresenter>
	{
	}

	@Inject
	RevokeItemFromUsersPresenter(EventBus eventBus, MyView view, MyProxy proxy,
			final PlaceManager placeManager, final CurrentUser currentUser)
	{
		super(eventBus, view, proxy, placeManager, currentUser);
		getView().setUiHandlers(this);
	}

	protected void onBind()
	{
    	onBind(() -> getView().getDataGrid(), dto -> enableButtons(dto.size() > 0), grid -> showUserList(selectionModel, grid, this));
	}

	protected void onReveal()
	{
		super.onReveal();
	}

	protected void onHide()
	{
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
		showContainerGroup(true);
		showMessage("");
        getView().getSendNotificationEmailSwitch().setValue(false);
		clearCheckBoxSelections();
		getView().getSearchTextBox().setValue("");
		enableButtons(false);
		adjustButtonsWidth();
		updateButtons();
		fetchAndPopulateForm();
		refreshDataGrid();
		showMessage("");
		DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
		ClientUtils.adjustDataGridHeight(grid);
		ClientUtils.addWindowResizeHandler(grid);
        grid.setColumnWidth(0, "55px");
	}
	
	private void adjustButtonsWidth()
	{
		/*
		ClientUtils.adjustButtonsWidth(
				getView().getRevokeButton(),
				getView().getClearButton(),
				getView().getListButton(),
				getView().getHelpButton());
				*/
	}
	
	private void updateButtons()
	{
		enableButtons(false);
		enableUnshareButton(false);
		ObidosMessages lang = ObidosMessages.LANG;
		
		getView().getContainerRow().setVisible(true);
		getView().getItemNameLabel().setText(lang.itemNameLabel());
		String action = ClientUtils.getActionFromUrl(placeManager);
		if (action == null)
		{
			return;
		}
		if (ObidosConstants.REVOKE_NOTE_SHARING.equals(action))
		{
			getView().getPanelHeader().setHeadingText(lang.revokeSharingANoteFromUsers());
			getView().getContainerRow().setVisible(false);
			getView().getItemNameLabel().setText(lang.noteNameLabel());
		}
		else if (ObidosConstants.REVOKE_ITEM_SHARING.equals(action))
		{
			getView().getPanelHeader().setHeadingText(lang.revokeSharingAItemFromUsers());
		}
	}

	@Override
	protected String getIdName()
	{
		return ObidosConstants.ITEM_ID;
	}

	// do not clear checkboxes in this method
	public void refreshDataGrid()
	{
		DataGrid<LimitedUserDTO> grid = getView().getDataGrid();
		grid.setVisibleRangeAndClearData(grid.getVisibleRange(), true);
	}

	private void enableButtons(boolean enabled)
	{
		getView().getRevokeButton().setEnabled(enabled);
		getView().getClearButton().setEnabled(enabled);
	}

	//private void numberOfUsersSelected()
	//{
		//Set<LimitedUserDTO> selectedSet = selectionModel.getSelectedSet();
		//int n = selectedSet.size();
		//if (n > 0)
		//{
			//showMessage("Number of users selected: " + n);
		//}

	//}

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

            	if (dto != null && dto.getSelected() == null)
            	{
            		dto.setSelected(false);
            	}
				setSearchedCheckbox(selectionModel, dto);
				return selectionModel.isSelected(dto);
			}
		};
		grid.addColumn(checkColumn, glang.select());
		
		// fullname
        ObidosButtonCell fullnameCell = new ObidosButtonCell(ObidosConstants.CELL_TYPE_FULLNAME);
        final Column<LimitedUserDTO, String> fullnameColumn = new Column<LimitedUserDTO, String>(fullnameCell)
        {

            @Override
            public String getValue(LimitedUserDTO dto)
            {
                return "n/a";
            }
        };
        grid.addColumn(fullnameColumn, glang.fullname());


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



		// Delete Button. Handler is at the bottom
		/*
		 * final Column<LimitedUserDTO, String> delCol = new
		 * Column<LimitedUserDTO, String> ( new ButtonCell(ButtonType.DANGER,
		 * IconType.REMOVE)) {
		 *
		 * @Override public String getValue(LimitedUserDTO userDTO) { return
		 * "Remove"; }
		 *
		 * }; grid.addColumn(delCol,"Remove User From Sharing List");
		 */

		AsyncDataProvider<LimitedUserDTO> dataProvider = new AsyncDataProvider<LimitedUserDTO>()
		{

			@Override
			protected void onRangeChanged(HasData<LimitedUserDTO> userDTO)
			{
				final Range range = userDTO.getVisibleRange();
				final int start = range.getStart();
				int length = range.getLength();
				gwtLog(" in AsyncDataProvider start: " + start);
				gwtLog("in AsyncDataProvider length: " + length);
				GwtAsyncWrapper<LimitedUserResult> callback = new GwtAsyncWrapper<LimitedUserResult>(source)
				{

					@Override
					public void uponFailure(Throwable e)
					{
						String message = e.getMessage();
						gwtLog("ERROR: " + message);
						updateRowCount(0, true);
					}

					@Override
					public void uponSuccess(LimitedUserResult userResult)
					{
						int numberOfUsers = userResult.getTotalUsers();
						gwtLog("Number of users: "+ numberOfUsers);
						List<LimitedUserDTO> users = userResult.getUsers();
						if (users != null && users.size() > 0)
						{
							/*
							gwtLog("UUUUUUUUUUUUUUUUUUUUUUUUUUU to: " + users.size() + " number: " + numberOfUsers);
							for (LimitedUserDTO u : users)
							{
								gwtLog(">>>> UU: " + u.getUsername());
								
							}
							*/
							updateRowCount(numberOfUsers, true);
							updateRowData(start, users);
						}
						else
						{
							messageLabel.setText("Could not find any more users to revoke ...");
							updateRowCount(0, true);
						}
					}
				};
				Long itemId = getItemIdFromUrl();
				if (itemId == null)
				{
					String key = ObidosConstants.ITEM_ID;
					showMessage("Could not get " + key + " from URL");
					return;
				}
				gwtLog("Item ID: " + itemId);
				String searchString = getView().getSearchTextBox().getValue();
				if (searchString.length() == 0)
				{
					searchString = null;
				}

				final ArrayList<Long> preSelectedUsers = getSelectedIds((MultiSelectionModel<LimitedUserDTO>) selectionModel);
				if (preSelectedUsers != null)
				{
					for (Long psuid : preSelectedUsers)
					{
						gwtLog(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  Sending Preselected ID: " + psuid);
					}
				}
				UserDTO userPatterns = new UserDTO();
				// Issue #387
				userPatterns.setFullname(searchString);

				AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
				UserService.Utility.getInstance().getUsersForItem(authCreds, itemId, userPatterns, SharedSetQuality.ONLY_SHARED_WITH, preSelectedUsers, start, length, toArray(OrderBy.FULLNAME_ASC), callback);
			} // end onRangeChanged()
		};
		
        fullnameColumn.setFieldUpdater(new FieldUpdater<LimitedUserDTO, String>()
        {

            @Override
            public void update(int idx, LimitedUserDTO dto, String value)
            {
            	ClientUtils.showUserInfo(placeManager, dto.getId(), getView().getMessageRow());
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

	private void enableUnshareButton(boolean enabled)
	{
		getView().getRevokeButton().setEnabled(enabled);

	}

	//private void setRevokeShareButtonTitle(String text)
	//{
		//getView().getRevokeButton().setText(text);
	//}

	private void fetchAndPopulateForm()
	{
		Long itemId = getItemIdFromUrl();
		if (itemId == null)
		{
			return;
		}


		TextBox itemNameTextBox = getView().getItemNameInputGroup().getTextBox();

		GwtAsyncWrapper<ItemDTO> callback = new GwtAsyncWrapper<ItemDTO>(this)
		{

			@Override
			public void uponSuccess(ItemDTO dto)
			{

				//FormGroup formGroup = getView().getFormGroup();

				List<UserDefinedTypeValueDTO> typeValues = dto.getValues();
				gwtLog("  TypeValues Size: " + typeValues.size());
				gwtLog("        Item name: " + dto.getName());
				gwtLog("        >>> ItemDTO id: " + dto.getId());

				Long templateId = dto.getValues().get(0).getUserDefinedTypeId();
				gwtLog(" Template id: " + templateId);
				// printFieldLabels(templateId);

				//Long id = dto.getValues().get(0).getId();
				itemNameTextBox.setValue(dto.getName());

				getView().getContainerNameInputGroup().setText(dto.getContainerName());

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

	//private void showHideContainerGroup()
	//{
		/*
		if (getContainerIdFromUrl() != null)
		{
			showContainerGroup(true);
		} else
		{
			showContainerGroup(false);
		}
		*/

	//}

	private void showContainerGroup(boolean visible)
	{
		getView().getContainerRow().setVisible(visible);
	}

	/*
	private void updateContainerNameInForm()
	{
		Long containerId = getContainerIdFromUrl();
		if (containerId == null)
		{
			showContainerGroup(false);
			getView().getListButton().setText(ObidosMessages.LANG.listMyItems());
			return;
		} else
		{
			showContainerGroup(true);
			getView().getListButton().setText(ObidosMessages.LANG.listItemsInContainer());
		}

		GwtAsyncWrapper<ContainerDTO> callback = new GwtAsyncWrapper<ContainerDTO>(this)
		{

			@Override
			public void uponSuccess(ContainerDTO result)
			{
				getView().getContainerNameTextBox().setValue(result.getName());
				Boolean privateContainer = result.getIsPrivate();
				if (privateContainer != null && privateContainer.booleanValue() == true)
				{
					getView().getContainerTypeTextBox().setValue("PRIVATE");
					getView().getPublicItemRadio().setValue(false);
					getView().getPrivateItemRadio().setValue(true);
					getView().getPrivateItemRadio().setEnabled(false);
					getView().getPublicItemRadio().setEnabled(false);
				} else
				{
					getView().getContainerTypeTextBox().setValue("PUBLIC");
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
	*/
	

// @Override
// public void listItems()
// {
// ClientUtils.navigateToPlace(placeManager);
// }

	@Override
	public void revokeSharingItemFromUsers()
	{
		promptRevokeItems();
	}

	@Override
	public void help()
	{
		showHelp();
	}

	@Override
	public void clearCheckBoxSelections()
	{
		clearCheckBoxSelections(selectionModel);
		showMessage("");
	}

	@Override
	public void searchUsers()
	{
		refreshDataGrid();
	}

    	@Override
    	public void showListItemsPage()
    	{
        	String nameToken = NameTokens.LIST_ITEMS;
        	ClientUtils.navigateToPlace(placeManager, nameToken);
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
	
	private void promptRevokeItems()
	{
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		String action = ClientUtils.getActionFromUrl(placeManager);
		String title = glang.revokeItem();
		String itemOrNote = ClientUtils.getSelectedTypeString(ids, ObidosConstants.ITEM_ID_N);
		if (ObidosConstants.REVOKE_NOTE_SHARING.equals(action))
		{
			title = glang.revokeNote();
			itemOrNote = ClientUtils.getSelectedTypeString(ids, ObidosConstants.NOTE_ID_N);
					
		}
		String isAre = glang.is();
		int n = ids.size();
        String user = glang.user();
		if (ids.size() > 1)
		{
			isAre = glang.are();
			user = glang.users();
		}
		String usersGroups = glang.users();
//		String message = glang.revokeSomethingWarning(n, itemOrNote, itemOrNote, usersGroups, itemOrNote, isAre, itemOrNote);
		String message = "You select " + n + " " + user + " to revoke sharing the Item from. Once revoked, the " + user + " will no longer have access to the Item.";
        message = message + "<br/>" + "<b>Are you sure you want to revoke sharing the Item?</b>";

        ClientUtils.promptForAction(() -> revokeSharingItemFromUsersReal(), title, message);
	}

	public void revokeSharingItemFromUsersReal()
	{
		Long itemId = getItemIdFromUrl();
		if (itemId == null)
		{
			return;
		}
		Set<LimitedUserDTO> dtoSet = selectionModel.getSelectedSet();
		if (dtoSet.size() == 0)
		{
			showErrorMessage("No users selected..."); // should not be
			return;
		}
		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				enableUnshareButton(false);
				// must clear CheckBox selections or they will accumulate
				clearCheckBoxSelections();
				refreshDataGrid();
//                Date date = new Date();
//                showMessage("Item revoked from " + dtoSet.size() + " users on " + date.toString());

		                String action = ClientUtils.getActionFromUrl(placeManager);
		                String entity = "Item";
		                if (ObidosConstants.REVOKE_NOTE_SHARING.equals(action))
		                {
		                     entity = "Note";
		                }
		                TextBox itemNameTextBox = getView().getItemNameInputGroup().getTextBox();
                                String itemName = itemNameTextBox.getValue();
                                showMessage(entity + " " + "<span style=\"color: purple;\">" + "'" + itemName + "'" + "</span>"
                                        + " revoked from " + "<span style=\"color: purple;\">" + dtoSet.size() + "</span>" + " user(s).");
			}

			@Override
			public void uponFailure(Throwable caught)
			{
                showErrorMessage("Could not revoke Item: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		String revokeString = getView().getRevokeCommentTextBox().getValue();
        PostOpActions postRevokeActions = null;
        if (getView().getSendNotificationEmailSwitch().getValue())
        {
        	gwtLog("Send email about revoking..........");
        	postRevokeActions = new PostOpActions();
        	postRevokeActions.setPostOpActions(PostOpActions.SEND_EMAIL);
        }
        else
        {
        	gwtLog("Dont send mail about revoking..");
        }

		ItemService.Utility.getInstance().revokeFromUsers(authCreds, itemId, getIdsFromDTOSet(dtoSet), revokeString, postRevokeActions, callback);
	}

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}
	
}
