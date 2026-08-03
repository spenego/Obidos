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

package com.spenego.Obidos.client.application.widgets;

import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.html.Span;

import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.spenego.Obidos.client.application.widgets.summernote.Summernote;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.util.ClientUtils;

public class ObidosNoteRow extends Row
{
	private boolean readOnly;
	private String noteText;
	private int noteHeight;
	private Summernote summernote;
	private FlowPanel noteDiv;
	
	@UiConstructor
	public ObidosNoteRow(boolean readOnly, String noteText, int noteHeight)
	{
		super();
		this.readOnly = readOnly;
		this.noteText = noteText;
		this.noteHeight = noteHeight;
		makeRow();
	}

	private void makeRow()
	{
		/*
		  	<Row>
		  		<FlowPanel>
		  			<Span/> - lock + Note
		  		</FlowPanel>
		  		<Row>
		  			<FlowPanel>
		  				<Summernote/>
		  			</FlowPanel>
		  		</Row>
		  	</Row>
		 */

		FlowPanel fp = new FlowPanel();
		add(fp);
		fp.addStyleName("col-sm-offset-3 col-sm-1");
		
		Span span = new Span();
		fp.add(span);
		ClientUtils.setNoteSpanHtml(span, ObidosMessages.LANG.note());
		
		Row row = new Row();
		add(row);

		fp  = new FlowPanel();
		row.add(fp);
		fp.addStyleName("col-sm-7");

		// full width
//		ClientUtils.setSummernoteWidth(fp);
		
		this.noteDiv = fp;

		this.summernote = ClientUtils.createSummernote(noteHeight);
		fp.add(this.summernote);
		this.summernote.setEnabled(!readOnly);
		this.summernote.setCode(noteText);
	}
	
	public Summernote getSummernote()
	{
		return this.summernote;
	}

	public String getNoteText()
	{
		return this.summernote.getCode();
	}

	public void setNoteText(String noteText)
	{
		this.noteText = noteText;
	}

	public int getNoteHeight()
	{
		return noteHeight;
	}

	public void setNoteHeight(int noteHeight)
	{
		this.noteHeight = noteHeight;
	}

	public FlowPanel getNoteDiv()
	{
		return noteDiv;
	}

	public void setNoteDiv(FlowPanel noteDiv)
	{
		this.noteDiv = noteDiv;
	}

}
