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

package com.spenego.Obidos.client.application.notificationmessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Code;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.InlineCheckBox;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionChangeEvent;
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
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.NotificationService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.NotificationDTO;
import com.spenego.Obidos.shared.dto.NotificationsResult;

public class NotificationMessagePresenter extends
		ObidosPresenter<NotificationDTO, NotificationMessagePresenter.MyView, NotificationMessagePresenter.MyProxy, NotificationMessageUiHandlers>
		implements NotificationMessageUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	private int nSelected = -1;
	private boolean mSelected = false;
	private boolean sShowUnreadMessages = true;

	interface MyView extends View, HasUiHandlers<NotificationMessageUiHandlers>
	{
		public DataGrid<NotificationDTO> getDataGrid();
		public SimplePager getPager();
		public BlockQuote getHelpBlockQuote();
		public Button getMarkReadButton();
		public Button getDeleteButton();
		public Row getSelectCheckBoxRow();
		public Button getSelectButton();
		public FormLabel getFormErrorLabel();
		public ObidosMessageRow getMessageRow();
		public ObidosPanelHeader getPanelHeader();
		public Button getUnreadMessageButton();
		public Button getReadMessageButton();
		public InlineCheckBox getMarkReadAfterViewCheckBox();
	}

	@NameToken(NameTokens.NOTIFICATION_MESSAGE)
	@ProxyCodeSplit
	@UseGatekeeper(LoggedInGatekeeper.class)
	interface MyProxy extends ProxyPlace<NotificationMessagePresenter>
	{
	}

	@Inject
	NotificationMessagePresenter(EventBus eventBus, MyView view, MyProxy proxy, final PlaceManager placeManager,
			final CurrentUser currentUser)
	{
		super(eventBus, view, proxy, placeManager, currentUser);

		getView().setUiHandlers(this);
	}

	protected void onBind()
	{
		super.onBind();
		DataGrid<NotificationDTO> grid = getView().getDataGrid();
		grid.setColumnWidth(0, "10%");
		grid.setColumnWidth(2, "20%");
		grid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		grid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<NotificationDTO> createCheckboxManager());

		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler()
		{

			@Override
			public void onSelectionChange(SelectionChangeEvent event)
			{
				Set<NotificationDTO> dtos = selectionModel.getSelectedSet();
				Button selectButton = getView().getSelectButton();
				int n = dtos.size();
				if (n > 0)
				{
					enableButtons(true);
					nSelected = n;
					selectButton.setText(glang.clear());
					selectButton.setIcon(IconType.ERASER);
					
					Button b = getView().getMarkReadButton();
					if (!sShowUnreadMessages)
					{
						b.setText(glang.markUnread());
						b.setTitle("Mark Message as Unread");
					}
					else
					{
						// show unread messages
						b.setVisible(true);
						b.setEnabled(true);
						b.setTitle("Mark Message as Read");
					}
				}
				else
				{
					showMessage("");
					nSelected = -1;
					enableButtons(false);
					selectButton.setText(glang.select());
					selectButton.setIcon(IconType.CHECK_SQUARE_O);
				}
			}
		});
		showMessageList(selectionModel, grid, this);
		selectCheckBoxByClickingOnTheRow(selectionModel, grid);
	}

	protected void onReveal()
	{
		super.onReveal();
	}

	protected void onHide()
	{
		super.onHide();
		clearForm();
	}

	protected void onUnbind()
	{
		super.onUnbind();
	}

	protected void onReset()
	{
		super.onReset();
		showMessage("&nbsp;");
		clearSelections();
		clearForm();
		enableButtons(false);
		setPanelHeaderColor();
		showUnreadMessages();
		DataGrid<NotificationDTO> grid = getView().getDataGrid();
		refreshDataGrid();
		ClientUtils.adjustDataGridHeight(grid);
		ClientUtils.addWindowResizeHandler(grid);
		setColumnWidth(grid, 0, "55px");
		getView().getMarkReadAfterViewCheckBox().setValue(false);
	}
	
	private void setPanelHeaderColor()
	{
		ObidosPanelHeader panelHeader = getView().getPanelHeader();
		ClientUtils.setPanelHeaderColor(panelHeader, currentUser);
	}
	
	private void clearForm()
	{
		nSelected = -1;
		mSelected = false;
		sShowUnreadMessages = true;
	}

	private void enableButtons(boolean enabled)
	{
		getView().getMarkReadButton().setEnabled(enabled);
		getView().getDeleteButton().setEnabled(enabled);
	}

	@Override
	public void help()
	{
		ClientUtils.showHelp(getView().getHelpBlockQuote());
	}

	@Override
	protected String getIdName()
	{
		return null;
	}

	@Override
	protected BlockQuote getHelpBlockQuote()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void refreshDataGrid()
	{
		DataGrid<NotificationDTO> grid = getView().getDataGrid();
		grid.setVisibleRangeAndClearData(grid.getVisibleRange(), true);
	}

	// TODO: This should be moved to ObidosPresenter
	private void setColumnUpdater(Column<NotificationDTO, String> col, final Consumer<NotificationDTO> consumer,
			final Supplier<String> passphraseDialogMessage)
	{
		col.setFieldUpdater(new FieldUpdater<NotificationDTO, String>()
		{
			@Override
			public void update(int idx, NotificationDTO dto, String value)
			{
				if (currentUser == null)
				{
					gwtLog("Ooops currentUser is null, it can't be!");
				}
				if (ClientUtils.isAdmin(currentUser))
				{
					return;
				}

				if (ClientUtils.isPassphraseRegistered(currentUser))
				{
					if (dto.getNameToken() != null)
					{
						consumer.accept(dto);
					}
				} else
				{
					ClientUtils.showRegisterPassphraseDialog(placeManager, passphraseDialogMessage.get(), null);
				}
			}
		});
	}

	private void showPage(NotificationDTO dto)
	{
		if (sShowUnreadMessages)
		{
			if (getView().getMarkReadAfterViewCheckBox().getValue())
			{
				gwtLog("Mark message as read...");
				markMessageAsRead(dto);
			}
			else
			{
				gwtLog("Will not mark message as read");
				
			}
		}

		ClientUtils.showPage(placeManager, dto.getNameToken(), dto.getWith());
	}

	private String getButtonText(Object dto)
	{
		return ((NotificationDTO) dto).getMessage();
	}

	private String getButtonLink(Object dto)
	{
		return ((NotificationDTO) dto).getNameToken();
	}
	
	private void showMessageList(final SelectionModel<NotificationDTO> selectionModel,
			final AbstractCellTable<NotificationDTO> grid, HasHandlers source)
	{
		ObidosMessages lang = ObidosMessages.LANG;
		grid.setAutoHeaderRefreshDisabled(true);
		grid.setAutoFooterRefreshDisabled(true);

		Code messageLabel = new Code();
		messageLabel.setText(lang.loading());
		grid.setEmptyTableWidget(messageLabel);

		addCheckBoxColumn(grid, dto -> selectionModelValue(selectionModel, dto));
		setColumnUpdater(addObidosButtonCellColumn(grid, dto -> dto.getMessage(), lang.message(),
				ObidosConstants.CELL_TYPE_VIEW_NOTIFICATION_SHARED, dto -> getButtonText(dto),
				dto -> getButtonLink(dto)), dto -> showPage(dto), () -> null); // handler
																				// for
																				// name
																				// column
		addDateTimeButtonCellColumn(grid);
		getView().getPager().setDisplay(grid);

		new AsyncDataProvider<NotificationDTO>()
		{
			@Override
			protected void onRangeChanged(HasData<NotificationDTO> dto)
			{
				final Range range = dto.getVisibleRange();
				final int start = range.getStart();
				int length = range.getLength();
				gwtLog(" in AsyncDataProvider start: " + start);
				gwtLog("in AsyncDataProvider length: " + length);
				GwtAsyncWrapper<NotificationsResult> callback = new GwtAsyncWrapper<NotificationsResult>(source)
				{

					@Override
					public void uponFailure(Throwable caught)
					{
						gwtLog("Exception caught: " + caught.getMessage());
						updateRowCount(0, true);
						showErrorMessage(caught.getMessage());
					}

					@Override
					public void uponSuccess(NotificationsResult result)
					{
						int unreadMessages = result.getUnreadMessageCount();
						int readMessages = result.getReadMessageCount();
						
						gwtLog("+> Unread messages: " + unreadMessages);
						gwtLog("+> Read messages: " + readMessages);

						setMessageBadges(unreadMessages, readMessages);

						List<NotificationDTO> messages = result.getElements();
						if (messages != null && messages.size() > 0)
						{
							updateRowCount(result.getTotal(), true);
							updateRowData(start, messages);
							String badgeText = "" + messages.size();
									
							if (sShowUnreadMessages)
							{
//								showMessage("Showing Unread Messages: " + sShowUnreadMessages);
								getView().getUnreadMessageButton().setBadgeText(badgeText);
							}
							else
							{
//								showMessage("Showing Read Messages: " + sShowUnreadMessages);
							}
						}
						else
						{
							if (sShowUnreadMessages)
							{
								messageLabel.setText("No unread messages found...");
							}
							else
							{
								messageLabel.setText("No read messages found...");
							}
							updateRowCount(0, true);
						}
					}
				};
				AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
				Boolean unread = sShowUnreadMessages;
				NotificationService.Utility.getInstance().getNotifications(authCreds, unread, start, length, toArray(OrderBy.CREATE_TIME_DESC), callback);
			}
		}.addDataDisplay(grid);
	}
	
	private void setMessageBadges(final int unread, final int read)
	{
		Button unreadButton = getView().getUnreadMessageButton();
		Button readButton = getView().getReadMessageButton();
		String unreadS = "" + unread;
		String readS = "" + read;
		unreadButton.setBadgeText(unreadS);
		readButton.setBadgeText(readS);
		unreadButton.setText(glang.unreadMessages());
		readButton.setText(glang.readMessages());
		if (unread  <= 1)
		{
			unreadButton.setText(glang.unreadMessage());
		}
		if (read <= 1 )
		{
			readButton.setText(glang.readMessage());
		}
		if (sShowUnreadMessages)
		{
			unreadButton.setType(ButtonType.SUCCESS);
			readButton.setType(ButtonType.DEFAULT);
		}
		else
		{
			readButton.setType(ButtonType.SUCCESS);
			unreadButton.setType(ButtonType.DEFAULT);
		}
	}

	private ArrayList<Long> getSelectedNotificationIds()
	{
		Set<NotificationDTO> s = selectionModel.getSelectedSet();
		final ArrayList<Long> list = new ArrayList<Long>(s.size());
		for (final NotificationDTO u : s)
		{
			list.add(u.getId());
		}
		return list;
	}

	public void clearSelections()
	{
		showMessage("");
		Set<NotificationDTO> selectedSet = selectionModel.getSelectedSet();
		if (selectedSet.size() > 0)
		{
			for (NotificationDTO dto : selectedSet)
			{
				selectionModel.setSelected(dto, false);
			}
		}
	}

	private void selectAll()
	{
		for (NotificationDTO dto : getView().getDataGrid().getVisibleItems())
		{
			selectionModel.setSelected(dto, true);
		}
	}

	//
	@Override
	public void selectButtonCallback()
	{
		Button selectButton = getView().getSelectButton();
		int n = getNumberOfSelectedMessages();
		if (n > 0)
		{
			// clear
			clearSelections();
		}
		else
		{
			selectAll();
		}
	}
	
	private void markMessageAsRead(NotificationDTO dto)
	{
		ArrayList<Long> ids = new ArrayList<Long>();
		ids.add(dto.getId());
		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				gwtLog("Message marked as read..");
				clearSelections();
				refreshDataGrid();
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				ObidosMessages lang = ObidosMessages.LANG;
				showErrorMessage("Error: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		NotificationService.Utility.getInstance().markRead(authCreds, ids, callback);

		
	}

	@Override
	public void markReadUnread()
	{
		ArrayList<Long> ids = getSelectedNotificationIds();
		if (ids.size() == 0)
		{
			gwtLog("No messages selected...");
			return;
		}
		for (Long long1 : ids)
		{
			gwtLog("Mark read id: " + long1);
		}

		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				clearSelections();
				refreshDataGrid();
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				ObidosMessages lang = ObidosMessages.LANG;
				showErrorMessage("Error: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		if (sShowUnreadMessages)
		{
			gwtLog("Mark messages as Read...........");
			NotificationService.Utility.getInstance().markRead(authCreds, ids, callback);
		}
		else
		{
			gwtLog("Mark messages as Unread...........");
			NotificationService.Utility.getInstance().markUnRead(authCreds, ids, callback);
		}
	}

	@Override
	public void delete()
	{
		if (!ClientUtils.doesLicenseAllowToCreateModify(currentUser, false))
		{
			return;
		}

		promptDeleteMessages();
	}

	private int getNumberOfSelectedMessages()
	{
		Set<NotificationDTO> dtos = selectionModel.getSelectedSet();
		return dtos.size();
	}

	@Override
	public void showReadMessages()
	{
		clearSelections();
		sShowUnreadMessages = false;

		Button b = getView().getMarkReadButton();
		b.setText(glang.markUnread());
		b.setVisible(true);
		if (getNumberOfSelectedMessages() > 0)
		{
			b.setEnabled(true);
			b.setTitle("");
		}
		refreshDataGrid();
	}
	
	@Override
	public void showUnreadMessages()
	{
		clearSelections();
		sShowUnreadMessages = true;
		
		Button b = getView().getMarkReadButton();
		b.setText(glang.markRead());
		b.setVisible(true);
		if (getNumberOfSelectedMessages() > 0)
		{
			b.setEnabled(true);
			b.setTitle("");
		}
		refreshDataGrid();
	}

    private void promptDeleteMessages()
    {
		ArrayList<Long> ids = getSelectedIds(selectionModel);
		String s = ClientUtils.getSelectedTypeString(ids, ObidosConstants.NOTIFICATION_MESSAGE_ID_N);
		String title = glang.deleteNotificationMessage();
		String message = glang.deleteTemplatesWarning(ids.size(), s, s, s);

       ClientUtils.promptForAction(() -> deleteMessagesReal(), title, message);
    }
	    
	private void deleteMessagesReal()
	{
		ArrayList<Long> ids = getSelectedNotificationIds();
		if (ids.size() == 0)
		{
			return;
		}

		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				clearSelections();
				refreshDataGrid();
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not delete message: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		NotificationService.Utility.getInstance().delete(authCreds, ids, callback);
	}

	@Override
	protected ObidosMessageRow getObidosMessageRow()
	{
		return getView().getMessageRow();
	}
	    


}