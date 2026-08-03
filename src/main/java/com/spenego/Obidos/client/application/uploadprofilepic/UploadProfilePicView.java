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

package com.spenego.Obidos.client.application.uploadprofilepic;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.TextBox;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.spenego.Obidos.client.application.widgets.ObidosImageUpload;
import com.spenego.Obidos.client.application.widgets.ObidosIntegerTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;

class UploadProfilePicView extends ViewWithUiHandlers<UploadProfilePicUiHandlers>
		implements UploadProfilePicPresenter.MyView
{
	@UiField
	BlockQuote helpBlockQuote;
	
	
	@UiField
	ObidosPanelHeader panelHeader;
	
	@UiField
	Image previewImage;

	@UiField
	Image histogramImage;

	@UiField
	ObidosImageUpload  imageUpload;
	
	@UiField
	ObidosMessageRow messageRow;
	
	@UiField
	SimplePanel cropperSimplePanel;

	@UiField(provided=true)
	Canvas cropperCanvas = Canvas.createIfSupported();

	@UiField(provided=true)
	Canvas previewImageCanvas = Canvas.createIfSupported();

	@UiField(provided=true)
	Canvas histogramCanvas = Canvas.createIfSupported();
	
	@UiField
	Image croppedImageResizer;
	
	@UiField
	Image loadedImageResizer;

	@UiField
	Image previewImageResizer;

	@UiField
	Button uploadButton;
	
	@UiField
	Button cropButton;

	@UiField
	Button annotateButton;

	@UiField
	TextBox annotateTextBox;
	
	@UiField
	CheckBox applyToRegionCheckBox;
	
	@UiField
	Button revertButton;

	@UiField
	Button grayButton;

	@UiField
	Button sepiaButton;

	@UiField
	Button brightenButton;

	@UiField
	Button darkenButton;

	@UiField
	Button negativeButton;
	
	@UiField
	Button solarizeButton;

	@UiField
	Button noiseButton;

	@UiField
	Label annotateLabel;
	
	@UiField
	TextBox annotateColorTextBox;
	
	@UiField
	ObidosIntegerTextBox annotateFontIntegerTextBox;

	@UiField
	Button histogramButton;

	@UiField
	Button redButton;

	@UiField
	Button greenButton;

	@UiField
	Button blueButton;

	interface Binder extends UiBinder<Widget, UploadProfilePicView>
	{
	}

	@Inject
	UploadProfilePicView(Binder uiBinder)
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		Button helpButton = panelHeader.getHelpButton();
		Button backButton = panelHeader.getBackButton();
		if (helpButton != null)
		{
			helpButton.addClickHandler(new ClickHandler()
			{

				@Override
				public void onClick(ClickEvent event)
				{
					getUiHandlers().help();
				}
			});
		}

		if (backButton != null)
		{
			backButton.addClickHandler(new ClickHandler()
			{

				@Override
				public void onClick(ClickEvent event)
				{
					getUiHandlers().back();
				}
			});
		}
	}

	public BlockQuote getHelpBlockQuote()
	{
		return helpBlockQuote;
	}

	public Canvas getCropperCanvas()
	{
		return cropperCanvas;
	}

	public Canvas getPreviewImageCanvas()
	{
		return previewImageCanvas;
	}

	public ObidosPanelHeader getPanelHeader()
	{
		return panelHeader;
	}

	public Image getPreviewImage()
	{
		return previewImage;
	}

	public ObidosImageUpload getImageUpload()
	{
		return imageUpload;
	}

	public ObidosMessageRow getMessageRow()
	{
		return messageRow;
	}

	public SimplePanel getCropperSimplePanel()
	{
		return cropperSimplePanel;
	}

	public Image getCroppedImageResizer()
	{
		return croppedImageResizer;
	}

	public Image getLoadedImageResizer()
	{
		return loadedImageResizer;
	}

	public Button getUploadButton()
	{
		return uploadButton;
	}

	public Button getCropButton()
	{
		return cropButton;
	}

	public Button getAnnotateButton()
	{
		return annotateButton;
	}

	public TextBox getAnnotateTextBox()
	{
		return annotateTextBox;
	}

	public CheckBox getApplyToRegionCheckBox()
	{
		return applyToRegionCheckBox;
	}

	public Button getRevertButton()
	{
		return revertButton;
	}

	public Button getGrayButton()
	{
		return grayButton;
	}

	public Button getSepiaButton()
	{
		return sepiaButton;
	}

	public Button getBrightenButton()
	{
		return brightenButton;
	}

	public Button getDarkenButton()
	{
		return darkenButton;
	}

	public Button getNegativeButton()
	{
		return negativeButton;
	}

	public Button getSolarizeButton()
	{
		return solarizeButton;
	}

	public Image getPreviewImageResizer()
	{
		return previewImageResizer;
	}
	@UiHandler("imageUpload")
	void onclickImageUpload (ChangeEvent e)
	{
		getUiHandlers().readUploadingImageFile();
	}
	
	@UiHandler("cropButton")
	void onclickCropButton (ClickEvent e)
	{
		getUiHandlers().cropImage();
	}
	
	@UiHandler("grayButton")
	void onclickGrayButton (ClickEvent e)
	{
		getUiHandlers().grayScaleImage();
	}

	@UiHandler("sepiaButton")
	void onclickSepiaButton (ClickEvent e)
	{
		getUiHandlers().sepiaImage();
	}

	@UiHandler("noiseButton")
	void onclickNoiseButton (ClickEvent e)
	{
		getUiHandlers().noiseImage();
	}

	@UiHandler("brightenButton")
	void onclickBrightenButton (ClickEvent e)
	{
		getUiHandlers().brightenImage();
	}
	@UiHandler("darkenButton")
	void onclickDarkenButton (ClickEvent e)
	{
		getUiHandlers().darkenImage();
	}

	@UiHandler("solarizeButton")
	void onclickSolarizeButton (ClickEvent e)
	{
		getUiHandlers().solarizeImage();
	}

	@UiHandler("negativeButton")
	void onclickNegateeButton (ClickEvent e)
	{
		getUiHandlers().negateImage();
	}

	@UiHandler("revertButton")
	void onclickRevertButton (ClickEvent e)
	{
		getUiHandlers().revertFilters();
	}
	
	@UiHandler("uploadButton")
	void onclickUoloadButton (ClickEvent e)
	{
		getUiHandlers().uploadImage(false);
	}

	@UiHandler("previewImage")
	void onclickPreviewImage(ClickEvent e)
	{
		getUiHandlers().priviewImageClickHandler(e);
	}

	@UiHandler("annotateButton")
	void onclickAnnotateButton(ClickEvent e)
	{
		getUiHandlers().annotateImage();
	}
	
	@UiHandler("histogramButton")
	void onclickHisgramButton (ClickEvent e)
	{
		getUiHandlers().histogramImage();
	}
	
	@UiHandler("redButton")
	void onclickRedButton (ClickEvent e)
	{
		getUiHandlers().redImage();
	}

	@UiHandler("greenButton")
	void onclickGreenButton (ClickEvent e)
	{
		getUiHandlers().greenImage();
	}
	@UiHandler("blueButton")
	void onclickBlueButton (ClickEvent e)
	{
		getUiHandlers().blueImage();
	}


	public Button getNoiseButton()
	{
		return noiseButton;
	}

	public Label getAnnotateLabel()
	{
		return annotateLabel;
	}

	public TextBox getAnnotateColorTextBox()
	{
		return annotateColorTextBox;
	}

	public ObidosIntegerTextBox getAnnotateFontIntegerTextBox()
	{
		return annotateFontIntegerTextBox;
	}

	public Image getHistogramImage()
	{
		return histogramImage;
	}

	public Canvas getHistogramCanvas()
	{
		return histogramCanvas;
	}

	public Button getHistogramButton()
	{
		return histogramButton;
	}

	public Button getRedButton()
	{
		return redButton;
	}

	public Button getGreenButton()
	{
		return greenButton;
	}

	public Button getBlueButton()
	{
		return blueButton;
	}
}