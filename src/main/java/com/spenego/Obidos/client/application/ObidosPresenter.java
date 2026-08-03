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

package com.spenego.Obidos.client.application;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.DefaultSelectionEventManager.SelectAction;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.presenter.slots.NestedSlot;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.summernote.Summernote;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ObidosButtonCell;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.OrderBy;
import com.spenego.Obidos.shared.Selectable;
import com.spenego.Obidos.shared.dto.HasCreatedAt;
import com.spenego.Obidos.shared.dto.HasId;
import com.spenego.Obidos.shared.dto.LimitedUserDTO;
import com.spenego.Obidos.shared.exceptions.ParamNotFoundException;

/**
 * A place to put code common to all Obidos Presenters. Initially created for
 * toArray.
 *
 * @author mmorgan
 *
 * @param <Z>
 * @param <T>
 * @param <V>
 * @param <Y>
 */
public abstract class ObidosPresenter<Z extends HasId, T extends View & HasUiHandlers<Y>, V extends Proxy<?>, Y extends UiHandlers> extends Presenter<T, V> {
	final DateTimeFormat dtf = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT);
	protected final PlaceManager placeManager;
	protected OrderBy sOrderBy;

	protected final CurrentUser currentUser;

	protected abstract String getIdName();

	protected abstract ObidosMessageRow getObidosMessageRow();

	protected abstract BlockQuote getHelpBlockQuote();
	
	public interface HasSearchTextBoxView {
		TextBox getSearchTextBox();
	}

    final protected MultiSelectionModel<Z> selectionModel = new MultiSelectionModel<Z>( new ProvidesKey<Z>() {
		@Override
	    public Object getKey(Z dto) {
	      return dto == null ? null : dto.getId();
	    }
	});

	public interface BaseView<B extends UiHandlers, C> extends View, HasUiHandlers<B> {
		DataGrid<C> getDataGrid();

		SimplePager getPager();

		FormLabel getFormErrorLabel();
	}

	public interface ShareRevokeView<B extends UiHandlers, C> extends BaseView<B, C>, HasSearchTextBoxView {
		Button getClearButton();

		Heading getPanelHeading();

		TextBox getShareContainerTextBox();
	}

	public interface NoteView<B extends UiHandlers, C> extends BaseView<B, C>, HasSearchTextBoxView {
		Button getSearchButton();

		Button getShareButton();

		Summernote getSummernote();

		TextBox getNoteNameTextBox();
	}

	public interface ItemShareView<B extends UiHandlers, C> extends BaseView<B, C>, HasSearchTextBoxView {
		Button getShareButton();

		FormLabel getNameLabel();

		TextBox getNameTextBox();
	};

	public interface ShareView<B extends UiHandlers, C> extends ShareRevokeView<B, C> {
		Button getShareContainerButton();

		TextBox getShareCommentTextBox();
	}

	public interface RevokeView<B extends UiHandlers, C> extends ShareRevokeView<B, C> {
		Button getRevokeButton();

		Button getSearchButton();

		TextBox getSearchTextBox();

		TextBox getRevokeCommentTextBox();
	}

	protected ObidosPresenter(final EventBus eventBus, final T view, final V proxy, 
			final PlaceManager placeManager, 
			final CurrentUser currentUser,
			final NestedSlot ns)
	{
		super(eventBus, view, proxy, ns);
		this.placeManager = placeManager;
		this.currentUser = currentUser;
	}

	protected ObidosPresenter(final EventBus eventBus, final T view, final V proxy, final PlaceManager placeManager,
			final CurrentUser currentUser)
	{
		this(eventBus, view, proxy, placeManager, currentUser, ApplicationPresenter.SLOT_MAIN);
        sOrderBy = OrderBy.UPDATE_TIME_DESC;
	}

	protected <X> ArrayList<X> toArray(final X t) {
		final ArrayList<X> list = new ArrayList<X>(1);
		list.add(t);
		return list;
	}

	protected void showMessage(final String message)
	{
		ClientUtils.showMessage(getObidosMessageRow(), message);
	}
	
	protected void showErrorMessage(final String errorMessage)
	{
		ClientUtils.showErrorMessage(getObidosMessageRow(), errorMessage);
	}

	protected void showHelp() {
		final BlockQuote helpBlockQuote = getHelpBlockQuote();
		if (helpBlockQuote != null) {
			ClientUtils.showHelp(helpBlockQuote);
		}
	}
	
	protected boolean isAdmin()
	{
		return ClientUtils.isAdmin(currentUser);
	}

	public final void gwtLog(final String message) {
		ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
	}

	protected final Long getIdFromUrl(final String id) throws NumberFormatException, ParamNotFoundException {
		return ClientUtils.getIdFromUrl(placeManager, id);
	}

	protected final Long getIdFromUrlNoEx(final String id) {
		try {
			return getIdFromUrl(id);
		} catch (NumberFormatException | ParamNotFoundException e) {
		}
		return null;
	}

	protected final Long getItemIdFromUrl() {
		return getIdFromUrlNoEx(ObidosConstants.ITEM_ID);
	}

	protected final Long getContainerIdFromUrl() {
		return getIdFromUrlNoEx(ObidosConstants.CONTAINER_ID);
	}

	protected final Long getId() {
		try {
			return getIdFromUrl(getIdName());
		} catch (NumberFormatException | ParamNotFoundException e) {
			showErrorMessage("Could not find " + getIdName() + " in URL");
			return null;
		}
	}

	protected final <S extends Selectable> ArrayList<Long> getIdsFromDTOSet(final Set<S> s) {
		final ArrayList<Long> list = new ArrayList<Long>(s.size());
		for (final S u : s) {
			list.add(u.getId());
		}
		return list;
	}

	protected final <S extends Selectable> ArrayList<Long> getSelectedIds(final MultiSelectionModel<S> selectionModel) {
		Set<S> selectedSet = selectionModel.getSelectedSet();
		if (selectedSet.size() > 0) {
			return getIdsFromDTOSet(selectedSet);
		}
		return null;
	}

	/**
	 * Select Checkbox if the user is previously selected the very first time
	 * the screen is rendered. Using API done for #227
	 *
	 * @param selectionModel
	 * @param dto
	 *            <p>
	 * @author spgdev@spenego.com - Sep 8, 2018
	 */
	protected static <S extends Selectable> void setSearchedCheckbox(final SelectionModel<S> selectionModel, final S dto) {
		if (dto != null) {
			if (dto.getSelected() == null) {
				dto.setSelected(false);
			} else if (dto.getSelected() && !selectionModel.isSelected(dto)) { // first
																				// time
				dto.setSelected(false);
				selectionModel.setSelected(dto, true);
			}
		}
	}

	protected final boolean previouslySelected(LimitedUserDTO dto) {
		return dto != null && dto.getSelected() != null && dto.getSelected();
	}

	protected <S extends Selectable> void clearCheckBoxSelections(final MultiSelectionModel<S> selectionModel) {
		Set<S> selectedSet = selectionModel.getSelectedSet();
		if (selectedSet.size() > 0) {
			for (S dto : selectedSet) {
				dto.setSelected(false);
				selectionModel.setSelected(dto, false);
			}
		}
	}

	protected final void printSelectedUsers(final MultiSelectionModel<LimitedUserDTO> selectionModel) {
		Set<LimitedUserDTO> selectedSet = selectionModel.getSelectedSet();
		if (selectedSet.size() > 0) {
			gwtLog("(debug) - Select Users --- starts: " + selectedSet.size());
			for (LimitedUserDTO dto : selectedSet) {
				gwtLog("(debug) - " + dto.getId() + "," + dto.getFullname() + " selected: " + dto.getSelected());
			}
			gwtLog("(debug) - Select Users --- ends");
		} else {
			gwtLog("(debug) - no users selected");
		}
	}

	/**
	 * select CheckBox by clicking on the row
	 *
	 * https://stackoverflow.com/questions/5637598/gwt-celltable-with-checkbox-selection-and-on-row-click-event
	 * https://www.programcreek.com/java-api-examples/index.php?api=com.google.gwt.view.client.CellPreviewEvent
	 * 
	 * Note: Our CheckBox is always at column 1, so if the click comes from any
	 * other column, ignore the click. It is very cool, we still can select
	 * CheckBox by clicking on the row!! Awesome!! -- spgdev@spenego.com - Mar
	 * 30, 2019
	 *
	 * @param sm
	 * @param grid
	 */
	protected final void selectCheckBoxByClickingOnTheRow(final SelectionModel<Z> sm, final DataGrid<Z> grid) {
		grid.setSelectionModel(sm, DefaultSelectionEventManager.createCustomManager(new DefaultSelectionEventManager.CheckboxEventTranslator<Z>() {
			@Override
			public SelectAction translateSelectionEvent(final CellPreviewEvent<Z> event) {
				SelectAction action = super.translateSelectionEvent(event);

				int colnum = grid.getKeyboardSelectedColumn();
				if (colnum != 0) {
					// Note we can not cancel the event otherwise click
					// will be ignored on other clickable buttons
					return SelectAction.IGNORE;
				}

				if (action.equals(SelectAction.IGNORE)) {
					/*
					 * boolean wasSelected = sm.isSelected(event.getValue());
					 * sm.setSelected(event.getValue(), !wasSelected); //
					 * boolean isSelected = sm.isSelected(event.getValue());
					 * event.setCanceled(true); return SelectAction.IGNORE;
					 */
					return SelectAction.TOGGLE;
				}
				return action;
			}
		}));
	}

	/*
	 * protected final void selectCheckBoxByClickingOnTheRowX(final
	 * SelectionModel<Z> sm, final DataGrid<Z> grid) {
	 * grid.setSelectionModel(sm,
	 * DefaultSelectionEventManager.createCustomManager( new
	 * DefaultSelectionEventManager.CheckboxEventTranslator<Z>() {
	 * 
	 * @Override public SelectAction translateSelectionEvent
	 * (CellPreviewEvent<Z> event) { NativeEvent nativeEvent =
	 * event.getNativeEvent ();
	 * 
	 * // Determine if we clicked on a checkbox. Element target =
	 * nativeEvent.getEventTarget ().cast (); if ("input".equals
	 * (target.getTagName ().toLowerCase (Locale.ROOT))) {
	 * gwtLog("in checkbox"); final InputElement input = target.cast (); if
	 * ("checkbox".equals (input.getType ().toLowerCase (Locale.ROOT))) { //
	 * Synchronize the checkbox with the current selection state.
	 * input.setChecked (event.getDisplay ().getSelectionModel ().isSelected (
	 * event.getValue ())); return SelectAction.TOGGLE; } } else { if
	 * (BrowserEvents.CLICK.equals (nativeEvent.getType ())) { final
	 * InputElement input = target.cast (); gwtLog("Disabled? " +
	 * input.isDisabled()); return SelectAction.SELECT; } } return
	 * SelectAction.IGNORE; } })); }
	 */

	private static <S> Column<S, ?> addColToGrid(final AbstractCellTable<S> grid, final String colHeader, final Column<S,?> col) {
		grid.addColumn(col, colHeader);
		return col;
	}

	@SuppressWarnings("unchecked")
	protected static <S, T extends AbstractCell<String>> Column<S, String> addCellColumn(final AbstractCellTable<S> grid, final Function<S, String> cellValueSupplier, final String colHeader, final T cell) {
		return (Column<S, String>) addColToGrid(grid, colHeader, new Column<S, String>(cell) {
			@Override
			public final String getValue(final S dto) {
				return dto == null ? ObidosMessages.LANG.na() : cellValueSupplier.apply(dto);
			}});
	}

	protected static <S> Column<S, String> addObidosButtonCellColumn(final AbstractCellTable<S> grid, final Function<S, String> cellValueSupplier, final String colHeader, final int cellType) {
		return addCellColumn(grid, cellValueSupplier, colHeader, new ObidosButtonCell(cellType));
	}

	protected static <S> Column<S, String> addObidosButtonCellColumn(final AbstractCellTable<S> grid, final Function<S, String> cellValueSupplier, final String colHeader, final int cellType,
			Function<Object, String> valueFunction, Function<Object, String> linkFunction) {
		return addCellColumn(grid, cellValueSupplier, colHeader, new ObidosButtonCell(cellType, valueFunction, linkFunction));
	}

	@SuppressWarnings("unchecked")
	protected static <S> TextColumn<S> addTextColumn(final AbstractCellTable<S> grid, final Function<S, String> valueSupplier, final String colHeader) {
		return (TextColumn<S>) addColToGrid(grid, colHeader, new TextColumn<S>() {
			@Override
			public final String getValue(final S dto) {
				return dto == null ? ObidosMessages.LANG.na() : valueSupplier.apply(dto);
			}});
	}

	protected static <S extends Selectable> Column<S, ?> addCheckBoxColumn(final AbstractCellTable<S> grid, final Function<S, Boolean> valueSupplier) {
		return addColToGrid(grid, ObidosMessages.LANG.selectLabel(), new Column<S, Boolean>(new CheckboxCell(true, false)) {
			@Override
			public final Boolean getValue(S dto) {
				if (dto != null && dto.getSelected() == null) {
					dto.setSelected(false);
				}

				return valueSupplier.apply(dto);
			}});
	}

	protected static <S extends Selectable> Boolean selectionModelValue(final SelectionModel<S> selectionModel, final S dto) {
		setSearchedCheckbox(selectionModel, dto);

		return selectionModel.isSelected(dto);
	}
	
	protected void setColumnWidth(final DataGrid<Z> grid, int colNum, String width)
	{
		grid.setColumnWidth(colNum, width);
	}

	private String getCreateDate(final Object dto) {
		 return ((HasCreatedAt) dto).getCreatedAt().toString();
	}

	private String showShortDateTime(final Object dto) {
		return dtf.format(((HasCreatedAt) dto).getCreatedAt());
	}

	protected void addDateTimeButtonCellColumn(final AbstractCellTable<Z> grid) {
		addObidosButtonCellColumn(grid, dto -> "", ObidosMessages.LANG.date(), ObidosConstants.CELL_TYPE_GENERAL, o -> showShortDateTime(o), oo -> getCreateDate(oo));
	}

	protected void onBind(final Supplier<DataGrid<Z>> gridSupplier, final Consumer<Set<Z>> selectionChangeOperation, final Consumer<DataGrid<Z>> consumer) {
		super.onBind();

		final DataGrid<Z> grid = gridSupplier.get();
		grid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		grid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<Z>createCheckboxManager());

		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				final Set<Z> selectedSet = selectionModel.getSelectedSet();
				gwtLog("Selected: " + selectedSet.size());
				selectionChangeOperation.accept(selectedSet);
			}
		});

		consumer.accept(grid);
		selectCheckBoxByClickingOnTheRow(selectionModel, grid);
	}

    protected void onReset()
    {
    }
    
    protected ArrayList<OrderBy> getOrderByList()
	{
    	gwtLog("XX OrderBy: " + sOrderBy);
		return toArray(sOrderBy);
	}
    
    protected final void safeInvocationCall(Runnable runnable) {
    	try {
    		runnable.run();
    	} catch(Exception ex) {
    		gwtLog("Caught " + ex);
    		// We should pop up a dialog indicating that the call to the server failed
    	}
    }
}
