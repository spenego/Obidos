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

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.TextBox;
import org.vectomatic.file.File;
import org.vectomatic.file.FileList;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.FileUtils;
import org.vectomatic.file.events.ErrorEvent;
import org.vectomatic.file.events.ErrorHandler;
import org.vectomatic.file.events.LoadEndEvent;
import org.vectomatic.file.events.LoadEndHandler;
import org.vectomatic.file.events.LoadStartEvent;
import org.vectomatic.file.events.LoadStartHandler;
import org.vectomatic.file.events.ProgressEvent;
import org.vectomatic.file.events.ProgressHandler;

import com.google.code.gwt.crop.client.GWTCropper;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
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
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosImageUpload;
import com.spenego.Obidos.client.application.widgets.ObidosIntegerTextBox;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.client.util.ImageFilters;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.UserDTO;

class ResizedImageInfo
{
	private String imageDataUrl;
	private int width;
	private int height;
	private double aspectRatio;
	private boolean requiresCropping;
	private int annotateX;
	private int annotateY;
	private int cropX;
	private int cropY;
	private int cropWidth;
	private int cropHeight;
	private int previewWidth;
	private int previewHeight;

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public boolean isRequiresCropping()
	{
		return requiresCropping;
	}

	public void setRequiresCropping(boolean requiresCropping)
	{
		this.requiresCropping = requiresCropping;
	}

	public double getAspectRatio()
	{
		return aspectRatio;
	}

	public void setAspectRatio(double aspectRatio)
	{
		this.aspectRatio = aspectRatio;
	}

	public int getAnnotateX()
	{
		return annotateX;
	}

	public void setAnnotateX(int annotateX)
	{
		this.annotateX = annotateX;
	}

	public int getAnnotateY()
	{
		return annotateY;
	}

	public void setAnnotateY(int annotateY)
	{
		this.annotateY = annotateY;
	}

	public int getCropX()
	{
		return cropX;
	}

	public void setCropX(int cropX)
	{
		this.cropX = cropX;
	}

	public int getCropY()
	{
		return cropY;
	}

	public void setCropY(int cropY)
	{
		this.cropY = cropY;
	}

	public int getCropWidth()
	{
		return cropWidth;
	}

	public void setCropWidth(int cropWidth)
	{
		this.cropWidth = cropWidth;
	}

	public int getCropHeight()
	{
		return cropHeight;
	}

	public void setCropHeight(int cropHeight)
	{
		this.cropHeight = cropHeight;
	}

	public int getPreviewWidth()
	{
		return previewWidth;
	}

	public void setPreviewWidth(int previewWidth)
	{
		this.previewWidth = previewWidth;
	}

	public int getPreviewHeight()
	{
		return previewHeight;
	}

	public void setPreviewHeight(int previewHeight)
	{
		this.previewHeight = previewHeight;
	}

	public String getImageDataUrl()
	{
		return imageDataUrl;
	}

	public void setImageDataUrl(String imageDataUrl)
	{
		this.imageDataUrl = imageDataUrl;
	}

}

public class UploadProfilePicPresenter
		extends Presenter<UploadProfilePicPresenter.MyView, UploadProfilePicPresenter.MyProxy>
		implements UploadProfilePicUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	
	private FileReader sReader = new FileReader();
	private File sFile;
	private GWTCropper sCropper;
	private int sCropX;
	private int sCropY;
	private int sCropWidth;
	private int sCropHeight;
	private int sAnnotateX = -1;
	private int sAnnotateY = -1;
	private boolean sImageLoaded = false;
	private boolean sNewFile = false;

	interface MyView extends View, HasUiHandlers<UploadProfilePicUiHandlers>
	{
		public BlockQuote getHelpBlockQuote();
		public Canvas getCropperCanvas();
		public Canvas getPreviewImageCanvas();
		public ObidosPanelHeader getPanelHeader();
		public Image getPreviewImage();
		public Image getHistogramImage();
		public Canvas getHistogramCanvas();
		public ObidosImageUpload getImageUpload();
		public ObidosMessageRow getMessageRow();
		public SimplePanel getCropperSimplePanel();
		public Image getCroppedImageResizer();
		public Image getLoadedImageResizer();
		public Button getUploadButton();
		public Button getCropButton();
		public Button getAnnotateButton();
		public TextBox getAnnotateTextBox();
		public CheckBox getApplyToRegionCheckBox();
		public Button getHistogramButton();
		public Button getRevertButton();
		public Button getGrayButton();
		public Button getSepiaButton();
		public Button getBrightenButton();
		public Button getDarkenButton();
		public Button getNegativeButton();
		public Button getSolarizeButton();
		public Image getPreviewImageResizer();
		public Button getNoiseButton();
		public Label getAnnotateLabel();
		public TextBox getAnnotateColorTextBox();
		public ObidosIntegerTextBox getAnnotateFontIntegerTextBox();
		public Button getRedButton();
		public Button getGreenButton();
		public Button getBlueButton();
	}

	@NameToken(NameTokens.UPLOAD_PROFILE_PIC)
	@ProxyCodeSplit
    @UseGatekeeper(LoggedInGatekeeper.class)
	interface MyProxy extends ProxyPlace<UploadProfilePicPresenter>
	{
	}

	private final PlaceManager placeManager;
	private final CurrentUser currentUser;

	@Inject
	UploadProfilePicPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
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
	}

	protected void onReveal()
	{
		super.onReveal();
	}

	protected void onHide()
	{
		super.onHide();
		resetProfilePicUpload();
	}

	protected void onUnbind()
	{
		super.onUnbind();
	}

	protected void onReset()
	{
		super.onReset();
		resetForm();
		setupProfilePicUploadHandlers();
	}
	
	private void resetForm()
	{
		showMessage(null);
		showCropButton(false);
		enableUploadButon(false);
		enableFilterButtons(false);
		clearAllCanvases();
		showPreviewImage(null);
		showHistogramImage(false);
		getView().getApplyToRegionCheckBox().setValue(false);
		getView().getCropperSimplePanel().setVisible(false);
		getView().getAnnotateTextBox().clear();
		getView().getAnnotateColorTextBox().clear();
		getView().getAnnotateFontIntegerTextBox().reset();

	}
	
	private void showHistogramImage(boolean visible)
	{
		getView().getHistogramImage().setVisible(visible);
	}
	
	/**
	 * read the file content with HTML5 File API
	 */
	@Override
	public void readUploadingImageFile()
	{
		if (!ClientUtils.isHTML5FileApiSupported())
		{
			showErrorMessage("This browser does not support HTML5 File API");
			return;
		}

		// Get
		FileReader reader = getFileReader();

		FileList files = getView().getImageUpload().getFiles();
		if (files.getLength() == 0)
		{
			showErrorMessage("No files selected");
			return;
		}
		sFile = files.getItem(0);
		// save
		String type = sFile.getType();
		if (type.startsWith("image/"))
		{
			gwtLog("read as binary stirng");
			reader.readAsBinaryString(sFile);
		}
	}

	private void resetProfilePicUpload()
	{
		sImageLoaded = false;
	}

	private FileReader getFileReader()
	{
		return sReader;
	}

	private void setupProfilePicUploadHandlers()
	{
		// create our global first
		gwtLog("Reader " + sReader);
		FileReader reader = sReader;

		reader.addLoadEndHandler(new LoadEndHandler()
		{

			@Override
			public void onLoadEnd(LoadEndEvent event)
			{
				if (reader.getError() != null)
				{
					gwtLog("reader error");
					return;
				}

				showPreviewImage(null);
				File file = getFile();
				if (file == null)
				{
					gwtLog("Uload File object is null");
					return;
				}
				String url = FileUtils.createDataUrl(file.getType(), reader.getStringResult());
				sNewFile = true;
				loadImageToCropper(url);
			}
		});

		reader.addProgressHandler(new ProgressHandler()
		{

			@Override
			public void onProgress(ProgressEvent event)
			{
			}
		});

		reader.addLoadStartHandler(new LoadStartHandler()
		{

			@Override
			public void onLoadStart(LoadStartEvent event)
			{
			}
		});

		reader.addErrorHandler(new ErrorHandler()
		{

			@Override
			public void onError(ErrorEvent event)
			{
				gwtLog("ERRRRRRRRRRRRRRRRRRRRRRRRRRRRRROR");
			}
		});

	}

	@Override
	public void back()
	{
		if (sImageLoaded)
		{
//			String message = "Do you want to upload the profile picture before leaving the page?";
//			ClientUtils.showBootboxDialog("Warning", message);
			promptToUploadImage();
			return;
			
		}
		ClientUtils.goBack(placeManager);

	}

	@Override
	public void help()
	{
		ClientUtils.showHelp(getView().getHelpBlockQuote());
	}

	@Override
	public void uploadImage(boolean goback)
	{
		String url = getView().getPreviewImage().getUrl();
		Long userDTOId = ClientUtils.getIdFromUrl(placeManager);
		if (userDTOId == null)
		{
			showErrorMessage("Could not get User DTO Id in URL");
			return;
		}
		UserDTO dto = new UserDTO();
		dto.setId(userDTOId);
		dto.setProfilePic(url.getBytes());

		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				sImageLoaded = false;
				if (goback)
				{
					ClientUtils.goBack(placeManager);
				}
				else
				{
					showMessage("Profile picture saved successfully");
				}

			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not upload profile picture: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		UserService.Utility.getInstance().modifyUser(authCreds, dto, callback);

	}

	private ResizedImageInfo getPreviewResizeImageInfo(final Image image, final Canvas canvas)
	{
		// Image image = getView().getPreviewImageResizer();
		// Canvas canvas = getView().getPreviewImageCanvas();
		return getResizedDataUrl(image, canvas, ObidosConstants.PROFILE_PIC_MAX_WIDTH,
				ObidosConstants.PROFILE_PIC_MAX_HEIGHT);
	}
	
	private void showUnresizedPreviewImage(final ResizedImageInfo imageInfo)
	{
		Image image = getView().getPreviewImage();
		int w = imageInfo.getWidth();
		int h = imageInfo.getHeight();
		gwtLog("XXX  wxh: " + w + "x" + h);
				
		image.setWidth(w + "px");
		image.setHeight(h + "px");
		image.getElement().getStyle().setWidth(w, Unit.PX);
		image.getElement().getStyle().setHeight(h, Unit.PX);
		
		image.addLoadHandler(new LoadHandler()
		{
			@Override
			public void onLoad(LoadEvent event)
			{
				gwtLog("Image loaded.......");
				enableUploadButon(true);
				enableFilterButtons(true);
				Canvas canvas = getView().getPreviewImageCanvas();
				canvas.setWidth(w + "px");
				canvas.setHeight(h + "px");
				canvas.setCoordinateSpaceWidth(w);
				canvas.setCoordinateSpaceHeight(h);
				Context2d context = canvas.getContext2d();
				ImageElement resizedImage = ImageElement.as(image.getElement());
				context.drawImage(resizedImage, 0, 0, w, h);
			}
		});
		image.setUrl(imageInfo.getImageDataUrl());
	}

	/*
	 * draw the cropped rectangle to preview image
	 */
	@Override
	public void cropImage()
	{
		String dataUrl = getDataUrlFromCropperCanvas();
		if (dataUrl == null)
		{
			showErrorMessage("Could not create image data URL");
			return;
		}
		Image image = getView().getPreviewImageResizer();
		image.addLoadHandler(new LoadHandler()
		{
			@Override
			public void onLoad(LoadEvent event)
			{
				ResizedImageInfo imageInfo = getPreviewResizeImageInfo(image, getView().getPreviewImageCanvas());
				showPreviewImage(imageInfo);
			}
		});
		image.setUrl(dataUrl);
	}

	@Override
	public void downloadImage()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void downloadRegion()
	{
		// TODO Auto-generated method stub

	}
	
	private void setHistogramLabel(int maxr, int maxg, int maxb)
	{
		Canvas canvas = getView().getHistogramCanvas();
		Context2d ctx = canvas.getContext2d();
		ctx.save();
		ctx.setFillStyle("red");
		String font = 12 + "px Arial";
		ctx.setFont(font);
		int x = 256 - 5;
		int y = 10;
		String text = "R: " + maxr;
		ctx.fillText(text, x, y);
		
		y = y + 15; 
		text = "G: " + maxg;
		ctx.setFillStyle("green");
		ctx.fillText(text, x, y);
		
		y = y + 15;
		text = "B: " + maxb;
		ctx.fillText(text, x, y);


		ctx.restore();

	}

	@Override
	public void annotateImage()
	{
		gwtLog("annotate.....");
		String text = getView().getAnnotateTextBox().getText();
		if (text.length() == 0)
		{
			showErrorMessage("Pleast type text for annotation");
			return;
		}
		if (sAnnotateX == -1 && sAnnotateY == -1)
		{
			showErrorMessage("Please select text location by clicking on the preview image at the top of the page");
			return;
		}
		String textColor= getView().getAnnotateColorTextBox().getText();
		if (textColor.length() == 0)
		{
			textColor = "white";
		}

		Integer fs = getView().getAnnotateFontIntegerTextBox().getValue();
		int fontSize = ClientUtils.fromInteger(fs);
		gwtLog("Font size before: " + fontSize);
		if (fontSize == 0)
		{
			fontSize = 14;
		}
		if (fontSize > 40)
		{
			fontSize = 40;
		}
		gwtLog("Font size after: " + fontSize);
		
		gwtLog("ax,ay: "+ sAnnotateX + "," + sAnnotateY);
		
		Canvas canvas = getView().getPreviewImageCanvas();
		int cw = canvas.getCoordinateSpaceWidth();
		int ch = canvas.getCoordinateSpaceHeight();
		gwtLog("Canvas wxh: " + cw + "x" + ch);
		String url = canvas.toDataUrl();
//		gwtLog("Canvas url: " + url);
		Context2d ctx = canvas.getContext2d();
		ctx.save();
		ctx.setFillStyle(textColor);
		String font = fontSize + "px Arial";
		ctx.setFont(font);
		ctx.fillText(text, sAnnotateX, sAnnotateY);
		ctx.restore();
		String dataUrl = canvas.toDataUrl();
		showPreviewImage(dataUrl, cw, ch);
		showMessage(null);
	}

	@Override
	public void grayScaleImage()
	{
		Canvas canvas = getView().getCropperCanvas();
		if (canvas == null)
		{
			return;
		}
		saveCroppingRect();

		Context2d ctx = canvas.getContext2d();
		int x = 0;
		int y = 0;
		int width = canvas.getCoordinateSpaceWidth();
		int height = canvas.getCoordinateSpaceHeight();
		if (getView().getApplyToRegionCheckBox().getValue())
		{
			x = sCropper.getSelectionXCoordinate();
			y = sCropper.getSelectionYCoordinate();
			width = sCropper.getSelectionWidth();
			height = sCropper.getSelectionHeight();
		}

		ImageData imageData = ctx.getImageData(x, y, width, height);
		ImageFilters.grayScalePixels(imageData.getData());
		ctx.putImageData(imageData, x, y);
		String dataURL = canvas.toDataUrl("image/jpeg");
		restoreCropRect();
		loadImageToCropper(dataURL);
	}

	@Override
	public void sepiaImage()
	{
		Canvas canvas = getView().getCropperCanvas();
		if (canvas == null)
		{
			return;
		}
		saveCroppingRect();

		Context2d ctx = canvas.getContext2d();
		int x = 0;
		int y = 0;
		int width = canvas.getCoordinateSpaceWidth();
		int height = canvas.getCoordinateSpaceHeight();
		if (getView().getApplyToRegionCheckBox().getValue())
		{
			x = sCropper.getSelectionXCoordinate();
			y = sCropper.getSelectionYCoordinate();
			width = sCropper.getSelectionWidth();
			height = sCropper.getSelectionHeight();
		}

		ImageData imageData = ctx.getImageData(x, y, width, height);
		ImageFilters.sepiaPixels(imageData.getData(), 100);
		ctx.putImageData(imageData, x, y);
		String dataURL = canvas.toDataUrl("image/jpeg");
		restoreCropRect();
		loadImageToCropper(dataURL);

	}

	@Override
	public void noiseImage()
	{
		Canvas canvas = getView().getCropperCanvas();
		if (canvas == null)
		{
			return;
		}
		saveCroppingRect();

		Context2d ctx = canvas.getContext2d();
		int x = 0;
		int y = 0;
		int width = canvas.getCoordinateSpaceWidth();
		int height = canvas.getCoordinateSpaceHeight();
		if (getView().getApplyToRegionCheckBox().getValue())
		{
			x = sCropper.getSelectionXCoordinate();
			y = sCropper.getSelectionYCoordinate();
			width = sCropper.getSelectionWidth();
			height = sCropper.getSelectionHeight();
		}

		ImageData imageData = ctx.getImageData(x, y, width, height);
		ImageFilters.noisePixels(imageData.getData(), 40);
		ctx.putImageData(imageData, x, y);
		String dataURL = canvas.toDataUrl("image/jpeg");
		restoreCropRect();
		loadImageToCropper(dataURL);

	}

	@Override
	public void brightenImage()
	{
		Canvas canvas = getView().getCropperCanvas();
		if (canvas == null)
		{
			return;
		}
		saveCroppingRect();

		Context2d ctx = canvas.getContext2d();
		int x = 0;
		int y = 0;
		int width = canvas.getCoordinateSpaceWidth();
		int height = canvas.getCoordinateSpaceHeight();
		if (getView().getApplyToRegionCheckBox().getValue())
		{
			x = sCropper.getSelectionXCoordinate();
			y = sCropper.getSelectionYCoordinate();
			width = sCropper.getSelectionWidth();
			height = sCropper.getSelectionHeight();
		}

		ImageData imageData = ctx.getImageData(x, y, width, height);
		ImageFilters.brightenPixels(imageData.getData(), 20);
		ctx.putImageData(imageData, x, y);
		String dataURL = canvas.toDataUrl("image/jpeg");
		restoreCropRect();
		loadImageToCropper(dataURL);
	}

	@Override
	public void darkenImage()
	{
		Canvas canvas = getView().getCropperCanvas();
		if (canvas == null)
		{
			return;
		}
		saveCroppingRect();

		Context2d ctx = canvas.getContext2d();
		int x = 0;
		int y = 0;
		int width = canvas.getCoordinateSpaceWidth();
		int height = canvas.getCoordinateSpaceHeight();
		if (getView().getApplyToRegionCheckBox().getValue())
		{
			x = sCropper.getSelectionXCoordinate();
			y = sCropper.getSelectionYCoordinate();
			width = sCropper.getSelectionWidth();
			height = sCropper.getSelectionHeight();
		}

		ImageData imageData = ctx.getImageData(x, y, width, height);
		ImageFilters.brightenPixels(imageData.getData(), -20);
		ctx.putImageData(imageData, x, y);
		String dataURL = canvas.toDataUrl("image/jpeg");
		restoreCropRect();
		loadImageToCropper(dataURL);
	}

	@Override
	public void sharpenImage()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void negateImage()
	{
		Canvas canvas = getView().getCropperCanvas();
		if (canvas == null)
		{
			return;
		}
		saveCroppingRect();

		Context2d ctx = canvas.getContext2d();
		int x = 0;
		int y = 0;
		int width = canvas.getCoordinateSpaceWidth();
		int height = canvas.getCoordinateSpaceHeight();
		if (getView().getApplyToRegionCheckBox().getValue())
		{
			x = sCropper.getSelectionXCoordinate();
			y = sCropper.getSelectionYCoordinate();
			width = sCropper.getSelectionWidth();
			height = sCropper.getSelectionHeight();
		}

		ImageData imageData = ctx.getImageData(x, y, width, height);
		ImageFilters.negativePixels(imageData.getData());
		ctx.putImageData(imageData, x, y);
		String dataURL = canvas.toDataUrl("image/jpeg");
		restoreCropRect();
		loadImageToCropper(dataURL);
	}

	@Override
	public void solarizeImage()
	{
		Canvas canvas = getView().getCropperCanvas();
		if (canvas == null)
		{
			return;
		}
		saveCroppingRect();

		Context2d ctx = canvas.getContext2d();
		int x = 0;
		int y = 0;
		int width = canvas.getCoordinateSpaceWidth();
		int height = canvas.getCoordinateSpaceHeight();
		if (getView().getApplyToRegionCheckBox().getValue())
		{
			x = sCropper.getSelectionXCoordinate();
			y = sCropper.getSelectionYCoordinate();
			width = sCropper.getSelectionWidth();
			height = sCropper.getSelectionHeight();
		}

		ImageData imageData = ctx.getImageData(x, y, width, height);
		ImageFilters.solarizePixels(imageData.getData());
		ctx.putImageData(imageData, x, y);
		String dataURL = canvas.toDataUrl("image/jpeg");
		restoreCropRect();
		loadImageToCropper(dataURL);
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
	
	private void resetCroppingRect(GWTCropper cropper)
	{
		if (cropper != null)
		{
			sCropX = cropper.getSelectionXCoordinate();
			sCropY = cropper.getSelectionYCoordinate();
			sCropWidth = cropper.getSelectionWidth();
			sCropHeight = cropper.getSelectionHeight();
		}
	}

	// Imaging stuff....
	// create cropper each time, that's how this thing is designed, URL
	// needs to be given to a new cropper
	private GWTCropper createImageCropper(final String dataUrl, final double aspectRatio)
	{
		SimplePanel simplePanel = getView().getCropperSimplePanel();
		if (sCropper != null)
		{
			simplePanel.remove(sCropper);
			sCropper = null;
		}
		sCropper = new GWTCropper(dataUrl);
		sCropper.setAspectRatio(aspectRatio);
		showCropButton(true);
//		resetCroppingRect(sCropper);
		return sCropper;
	}

	private GWTCropper getImageCropper()
	{
		return sCropper;
	}

	private void setImageDimension(Image image, int w, int h, String dataUrl)
	{
		gwtLog("URL size: " + dataUrl.length());
				image.setWidth(w + "px");
				image.setHeight(h + "px");
				image.getElement().getStyle().setWidth(w, Unit.PX);
				image.getElement().getStyle().setHeight(h, Unit.PX);
		

		image.addLoadHandler(new LoadHandler()
		{
			
			@Override
			public void onLoad(LoadEvent event)
			{
				gwtLog("Set preview canvas...");

				Canvas canvas = getView().getPreviewImageCanvas();
				canvas.setWidth(w + "px");
				canvas.setHeight(h + "px");
				canvas.setCoordinateSpaceWidth(w);
				canvas.setCoordinateSpaceHeight(h);
				gwtLog("Preview canvas wxh: " + w + "x" + h);
				Context2d context = canvas.getContext2d();
				ImageElement resizedImage = ImageElement.as(image.getElement());
				context.drawImage(resizedImage, 0, 0, w, h);
			}
		});
		image.setUrl(dataUrl);
		
	}

	private void showPreviewImage(final String dataUrl, final int w, final int h)
	{
		Image image = getView().getPreviewImage();
		image.addLoadHandler(new LoadHandler()
		{
			@Override
			public void onLoad(LoadEvent event)
			{
				image.setWidth(w + "px");
				image.setHeight(h + "px");
				gwtLog("Preview wxh: "+ w + "x" + h);
				image.getElement().getStyle().setWidth(w, Unit.PX);
				image.getElement().getStyle().setHeight(h, Unit.PX);

				gwtLog("preview image loaded...");
			}
		});
		image.setUrl(dataUrl);

	}

	private void showPreviewImage(final ResizedImageInfo imageInfo)
	{
		gwtLog("Show preview image..");
		Image image = getView().getPreviewImage();
		Image histogramImage = getView().getHistogramImage();
		image.getElement().getStyle().setProperty("cursor", "text");
		int w = 0;
		int h = 0;
		String dataUrl = null;
		if (imageInfo == null)
		{
			showHistogramImage(false);
			w = ObidosConstants.PROFILE_PIC_MAX_WIDTH;
			h = ObidosConstants.PROFILE_PIC_MAX_HEIGHT;
			dataUrl = ObidosConstants.PROFILE_PLACEHOLDER_IMAGE;
			image.getElement().getStyle().setProperty("border", ObidosConstants.DEFAULT_PROFILE_IMAGE_STYPE);
			
			
			histogramImage.getElement().getStyle().setProperty("border", ObidosConstants.DEFAULT_PROFILE_IMAGE_STYPE);
			histogramImage.setWidth(w + "px");
			histogramImage.setHeight(h + "px");
			histogramImage.getElement().getStyle().setWidth(w, Unit.PX);
			histogramImage.getElement().getStyle().setHeight(h, Unit.PX);

			enableFilterButtons(false);
			enableUploadButon(false);
		}
		else
		{
			dataUrl = imageInfo.getImageDataUrl();
			w = imageInfo.getWidth();
			h = imageInfo.getHeight();
			image.getElement().getStyle().setProperty("border", ObidosConstants.LOADED_PROFILE_IMAGE_STYPE);
			enableUploadButon(true);
			enableFilterButtons(true);
			sImageLoaded = true;
			
			if (histogramImageVisible())
			{
				histogramImage();
			}
		}
		gwtLog("Preview wxh: "+ w + "x" + h);
		setImageDimension(image, w, h, dataUrl);
	}

	/**
	 * Calculate the new width and height keeping the aspect ratio. Draw the
	 * image on a HTML5 Canvas and return the resized data url
	 * 
	 * If original image dimension is smaller than new dimension, use the
	 * original dimension
	 * 
	 * @param image
	 *            - the image widget
	 * @param newWidth
	 *            - resize to this width
	 * @param newHeight
	 *            - resize to this height
	 * @return resized data Url
	 *         <p>
	 * @author spgdev@spenego.com - Oct 13, 2019
	 */
	private ResizedImageInfo getResizedDataUrl(Image image, Canvas canvas, int newWidth, int newHeight)
	{
		gwtLog("in ResizedDataUrl...");
		
		int width = image.getWidth();
		if (width == 0)
		{
			width = ClientUtils.ieWidth(image.getElement());
		}
		int height = image.getHeight();
		if (height == 0)
		{
			height = ClientUtils.ieHeight(image.getElement());
		}
		// Get
		File file = getFile();

		gwtLog("Loaded image name: " + file.getName());
		gwtLog("Size: " + file.getSize() + " bytes");
		gwtLog("Loaded image size: " + width + "x" + height);
		if (width < newWidth)
		{
			newWidth = width;
		}
		if (height < newHeight)
		{
			newHeight = height;
		}
		float aspectRatio = (float) ((width * 1.0) / (height * 1.0));
		gwtLog("Aspect radio: " + aspectRatio);
		newHeight = Math.round((newWidth / aspectRatio));
		newWidth = Math.round((newHeight * aspectRatio));

		int w = newWidth;
		int h = newHeight;
		gwtLog("Resized image size: " + w + "x" + h);

		// draw the image on the canvas to get the resized data
		// note: drawing image on a image widget does not make it lose its
		// data

		// Canvas canvas = getView().getCanvas();
		canvas.setWidth(w + "px");
		canvas.setHeight(h + "px");
		canvas.setCoordinateSpaceWidth(w);
		canvas.setCoordinateSpaceHeight(h);
		Context2d context = canvas.getContext2d();
		ImageElement resizedImage = ImageElement.as(image.getElement());
		context.drawImage(resizedImage, 0, 0, w, h);
		String resizedImageUrl = canvas.toDataUrl("image/jpeg");

		ResizedImageInfo imageInfo = new ResizedImageInfo();
		imageInfo.setImageDataUrl(resizedImageUrl);
		imageInfo.setWidth(w);
		imageInfo.setHeight(w);
		imageInfo.setRequiresCropping(true);
		imageInfo.setAspectRatio(aspectRatio);
		/*
		if (w <= ObidosConstants.PROFILE_PIC_MAX_WIDTH  || h <= ObidosConstants.PROFILE_PIC_MAX_HEIGHT)
		{
			gwtLog("Cropping not required: wxh: " + w + "x" + h);
			gwtLog("Setting: wxh: " + w + "x" + h);
			imageInfo.setWidth(w);
			imageInfo.setHeight(h);
			imageInfo.setRequiresCropping(false);
		}
		gwtLog("Requires cropping? " + imageInfo.isRequiresCropping());
		*/

		return imageInfo;
	}

	private void loadImageToCropper(final String imageDataUrl)
	{
		Image image = getView().getCroppedImageResizer();
		image.addLoadHandler(new LoadHandler()
		{

			@Override
			public void onLoad(LoadEvent event)
			{
				gwtLog("onLoad...");
				Canvas canvas = getView().getCropperCanvas();
				ResizedImageInfo imageInfo = getResizedDataUrl(image, canvas, 512, 512);
				gwtLog("ZZZZZZZZ " + imageInfo.getWidth() + "x" + imageInfo.getHeight());

				// Save

				SimplePanel simplePanel = getView().getCropperSimplePanel();
				if (imageInfo.isRequiresCropping())
				{
					gwtLog("Show cropper......");
					simplePanel.setVisible(true);
					GWTCropper imageCropper = createImageCropper(imageInfo.getImageDataUrl(), 1);
					simplePanel.add(imageCropper);
					if (sNewFile)
					{
						resetCroppingRect(imageCropper);
						sNewFile = false;
					}
					
					gwtLog("Image wxh: " + imageInfo.getWidth() + "x" + imageInfo.getHeight());
					gwtLog(" Crop wxh: " + sCropWidth + "x" + sCropHeight);
					restoreCropRect();
				}
				else
				{
					gwtLog("Show image as it is..");
					simplePanel.setVisible(false);
					//imageInfo = getPreviewResizeImageInfo(image, getView().getPreviewImageCanvas());
					//imageInfo = getResizedDataUrl(image, canvas, imageInfo.getWidth(), imageInfo.getHeight());
					//sPreviewWidth = imageInfo.getWidth();
					//sPreviewHeight = imageInfo.getHeight();
//					showPreviewImage(imageInfo);
					gwtLog("XXXXXXXX " + imageInfo.getWidth() + "x" + imageInfo.getHeight());
					showUnresizedPreviewImage(imageInfo);
				}

			}
		});
		image.setUrl(imageDataUrl);
	}

	private void dumpCropRect()
	{
		gwtLog("Crop x,y: " + sCropX + "," + sCropY);
		gwtLog("Crop wxh: " + sCropWidth + "x" + sCropHeight);

	}

	private void saveCroppingRect()
	{
		GWTCropper cropper = getImageCropper();
		if (cropper == null)
		{
			return;
		}
		sCropX = cropper.getSelectionXCoordinate();
		sCropY = cropper.getSelectionYCoordinate();
		sCropWidth = cropper.getSelectionWidth();
		sCropHeight = cropper.getSelectionHeight();

	}

	private void restoreCropRect()
	{
		GWTCropper cropper = getImageCropper();
		if (cropper == null)
		{
			return;
		}
		if (sCropWidth > 0 && sCropHeight > 0)
		{
			cropper.setInitialSelection(sCropX, sCropY, sCropWidth, sCropHeight);
		}

	}

	private File getFile()
	{
		return sFile;
	}

	// return url of selected area only
	private String getDataUrlFromCropperCanvas()
	{
		final GWTCropper cropper = sCropper;
		if (cropper == null)
		{
			return null;
		}

		int x = cropper.getSelectionXCoordinate();
		int y = cropper.getSelectionYCoordinate();
		int w = cropper.getSelectionWidth();
		int h = cropper.getSelectionHeight();

		gwtLog("Ccanvas x,y,w,h: " + x + "," + "," + y + "," + w + "," + h);
		saveCroppingRect();

		Canvas canvas = getView().getCropperCanvas();
		Context2d context = canvas.getContext2d();
		ImageData imageData = context.getImageData(x, y, w, h);

		// draw that image to our preview canvas
		Canvas previewCanvas = getView().getPreviewImageCanvas();
		Context2d previewContext = previewCanvas.getContext2d();
		previewCanvas.setWidth(w + "px");
		previewCanvas.setHeight(h + "px");
		previewCanvas.setCoordinateSpaceWidth(w);
		previewCanvas.setCoordinateSpaceHeight(h);

		previewContext.putImageData(imageData, 0, 0);
		return previewCanvas.toDataUrl("image/jpeg");
	}

	private void showCropButton(boolean visible)
	{
		getView().getCropButton().setVisible(visible);
		enableFilterButtons(visible);
		getView().getApplyToRegionCheckBox().setEnabled(visible);
	}

	private void enableUploadButon(boolean enabled)
	{
		getView().getUploadButton().setEnabled(enabled);
		getView().getAnnotateButton().setEnabled(enabled);
		getView().getAnnotateTextBox().setEnabled(enabled);
		getView().getAnnotateColorTextBox().setEnabled(enabled);
		getView().getAnnotateFontIntegerTextBox().setEnabled(enabled);
		getView().getHistogramButton().setEnabled(enabled);
	}

	private void enableFilterButtons(boolean enabled)
	{
		getView().getGrayButton().setEnabled(enabled);
		getView().getSepiaButton().setEnabled(enabled);
		getView().getBrightenButton().setEnabled(enabled);
		getView().getDarkenButton().setEnabled(enabled);
		getView().getNegativeButton().setEnabled(enabled);
		getView().getSolarizeButton().setEnabled(enabled);
		getView().getNoiseButton().setEnabled(enabled);
		getView().getRevertButton().setEnabled(enabled);
		getView().getAnnotateLabel().setText("x,y: -1, -1");
		getView().getRedButton().setEnabled(enabled);
		getView().getGreenButton().setEnabled(enabled);
		getView().getBlueButton().setEnabled(enabled);
	}

	private String getImageDataUrl()
	{
		String url = FileUtils.createDataUrl(sFile.getType(), sReader.getStringResult());
		return url;
	}

	@Override
	public void revertFilters()
	{
		String url = getImageDataUrl();
		loadImageToCropper(url);
	}

	@Override
	public void priviewImageClickHandler(ClickEvent e)
	{
		if (!getView().getAnnotateButton().isEnabled())
		{
			return;
		}
				
		Image image = getView().getPreviewImage();
		// https://stackoverflow.com/questions/17855096/gwt-how-to-retrieve-real-clicked-widget
		Element targetElem = Element.as(e.getNativeEvent().getEventTarget());
		Widget targetWidget = null;

		if (image.getElement().isOrHasChild(targetElem))
		{

			int sx = e.getNativeEvent().getClientX() + Document.get().getScrollLeft();
			int sy = e.getNativeEvent().getClientY() - image.getAbsoluteTop() + Document.get().getScrollTop();
			int x = e.getClientX();
			sx = x - image.getAbsoluteLeft();
			int y = e.getClientY();
			int w = image.getWidth();
			int h = image.getHeight();
			targetWidget = image;
			sAnnotateX = sx;
			sAnnotateY = sy;
			String text = sx + "," + sy;
			getView().getAnnotateLabel().setText(text);

		}
	}
	
	private void clearCanvas(final Canvas canvas)
	{
		if (canvas == null)
		{
			return;
		}
		int w = canvas.getCoordinateSpaceWidth();
		int h = canvas.getCoordinateSpaceHeight();
		canvas.getContext2d().clearRect(0, 0, w, h);
	}
	
	private void clearAllCanvases() 
	{
		clearCanvas(getView().getCropperCanvas());
		clearCanvas(getView().getPreviewImageCanvas());
	}
	
	private void promptToUploadImage()
	{
		String title = glang.imageNotSaved();
		String message = glang.confirmProfilePicUpload();
		ClientUtils.promptToSaveChange(() -> uploadImage(true), title, message, placeManager);
	}

	@Override
	public void histogramImage()
	{
		int imageWidth = 256;
		int imageHeight = ObidosConstants.PROFILE_PIC_MAX_HEIGHT;

		Canvas canvas = getView().getPreviewImageCanvas();
		int w = canvas.getCoordinateSpaceWidth();
		int h = canvas.getCoordinateSpaceHeight();
		Context2d ctx = canvas.getContext2d();
		ImageData imageData = ctx.getImageData(0, 0, w, h);
		CanvasPixelArray array = imageData.getData();

		int[] histor = new int[256];
		int[] histog = new int[256];
		int[] histob = new int[256];
		int maxr = 0;
		int maxg = 0;
		int maxb = 0;
		for (int i = 0; i < 256; i++)
		{
			histor[i] = 0;
			histog[i] = 0;
			histob[i] = 0;
		}
		
		for (int i = 0; i < array.getLength(); i += 4)
		{
			int r = array.get(i);
			int g = array.get(i + 1);
			int b = array.get(i + 2);

			/*
			if (r > 255) r = 255;
			if (g > 255) g = 255;
			if (b > 255) b = 255;
			if (r < 0) r = 0;
			if (g < 0) g = 0;
			if (b < 0) b = 0;
			*/

			histor[r]++;
			histog[g]++;
			histob[b]++;
			if (maxr < histor[r])
			{
				maxr = histor[r];
			}
			if (maxg < histog[g])
			{
				maxg = histog[g];
			}
			if (maxb < histob[b])
			{
				maxb = histob[b];
			}
		}
		gwtLog("maxr: " + maxr);
		gwtLog("maxg: " + maxg);
		gwtLog("maxb: " + maxb);
		
		
		// draw the histogram on canvas
		Canvas histoCanvas = getView().getHistogramCanvas();
		int canvasWidth = imageWidth;
		int canvasHeight = histoCanvas.getCoordinateSpaceHeight();
		Context2d ctxHisto = histoCanvas.getContext2d();
		ctxHisto.clearRect(0, 0, canvasWidth, canvasHeight);
		
		// white bg seems to look nice
//		ctxHisto.setFillStyle("black");
//		ctxHisto.fillRect(0, 0, canvasWidth, canvasHeight);

		ctxHisto.setStrokeStyle("red");
		gwtLog("histo canvas height: "+ canvasHeight);
		for (int x = 0; x < imageWidth; x++)
		{
			int barHeight = canvasHeight - (histor[x] * canvasHeight /maxr);
			ctxHisto.save();
			ctxHisto.beginPath();
			ctxHisto.moveTo(x, canvasHeight);
			ctxHisto.lineTo(x, barHeight);
			ctxHisto.stroke();
			ctxHisto.setGlobalAlpha(0.5);
			ctxHisto.restore();

		}

		ctxHisto.setStrokeStyle("green");
		for (int x = 0; x < imageWidth; x++)
		{
			int barHeight = canvasHeight - (histog[x] * canvasHeight /maxg);
			ctxHisto.save();
			ctxHisto.beginPath();
			ctxHisto.moveTo(x, canvasHeight);
			ctxHisto.lineTo(x, barHeight);
			ctxHisto.stroke();
			ctxHisto.setGlobalAlpha(0.5);
			ctxHisto.restore();

		}
		gwtLog("maxr: " + maxr + " maxg: " + maxg + " maxb: " + maxb);
		
		String color = "blue";
		
		if (maxr == maxg  && maxr == maxb)
		{
			color = "#ccc";
		}

		ctxHisto.setStrokeStyle(color);
		for (int x = 0; x < imageWidth; x++)
		{
			int barHeight = canvasHeight - (histob[x] * canvasHeight /maxb);
			ctxHisto.save();
			ctxHisto.beginPath();
			ctxHisto.moveTo(x, canvasHeight);
			ctxHisto.lineTo(x, barHeight);
			ctxHisto.stroke();
			ctxHisto.setGlobalAlpha(0.5);
			ctxHisto.restore();
		}
//		setHistogramLabel(maxr, maxg, maxb);

		String url = histoCanvas.toDataUrl();
		Image image = getView().getHistogramImage();
		image.setUrl(url);
		image.setWidth(imageWidth + "px");
		image.setHeight(imageHeight + "px");
		image.getElement().getStyle().setWidth(imageWidth, Unit.PX);
		image.getElement().getStyle().setHeight(imageHeight, Unit.PX);

		showHistogramImage(true);
	}
	
	private boolean histogramImageVisible()
	{
		return getView().getHistogramImage().isVisible();
	}
	

	@Override
	public void redImage()
	{
		Canvas canvas = getView().getCropperCanvas();
		if (canvas == null)
		{
			return;
		}
		saveCroppingRect();

		Context2d ctx = canvas.getContext2d();
		int x = 0;
		int y = 0;
		int width = canvas.getCoordinateSpaceWidth();
		int height = canvas.getCoordinateSpaceHeight();
		if (getView().getApplyToRegionCheckBox().getValue())
		{
			x = sCropper.getSelectionXCoordinate();
			y = sCropper.getSelectionYCoordinate();
			width = sCropper.getSelectionWidth();
			height = sCropper.getSelectionHeight();
		}

		ImageData imageData = ctx.getImageData(x, y, width, height);
		ImageFilters.redPixels(imageData.getData());
		ctx.putImageData(imageData, x, y);
		String dataURL = canvas.toDataUrl("image/jpeg");
		restoreCropRect();
		loadImageToCropper(dataURL);
	}

	@Override
	public void greenImage()
	{
		Canvas canvas = getView().getCropperCanvas();
		if (canvas == null)
		{
			return;
		}
		saveCroppingRect();

		Context2d ctx = canvas.getContext2d();
		int x = 0;
		int y = 0;
		int width = canvas.getCoordinateSpaceWidth();
		int height = canvas.getCoordinateSpaceHeight();
		if (getView().getApplyToRegionCheckBox().getValue())
		{
			x = sCropper.getSelectionXCoordinate();
			y = sCropper.getSelectionYCoordinate();
			width = sCropper.getSelectionWidth();
			height = sCropper.getSelectionHeight();
		}

		ImageData imageData = ctx.getImageData(x, y, width, height);
		ImageFilters.greenPixels(imageData.getData());
		ctx.putImageData(imageData, x, y);
		String dataURL = canvas.toDataUrl("image/jpeg");
		restoreCropRect();
		loadImageToCropper(dataURL);

		
	}

	@Override
	public void blueImage()
	{
		Canvas canvas = getView().getCropperCanvas();
		if (canvas == null)
		{
			return;
		}
		saveCroppingRect();

		Context2d ctx = canvas.getContext2d();
		int x = 0;
		int y = 0;
		int width = canvas.getCoordinateSpaceWidth();
		int height = canvas.getCoordinateSpaceHeight();
		if (getView().getApplyToRegionCheckBox().getValue())
		{
			x = sCropper.getSelectionXCoordinate();
			y = sCropper.getSelectionYCoordinate();
			width = sCropper.getSelectionWidth();
			height = sCropper.getSelectionHeight();
		}

		ImageData imageData = ctx.getImageData(x, y, width, height);
		ImageFilters.bluePixels(imageData.getData());
		ctx.putImageData(imageData, x, y);
		String dataURL = canvas.toDataUrl("image/jpeg");
		restoreCropRect();
		loadImageToCropper(dataURL);

		
	}
}