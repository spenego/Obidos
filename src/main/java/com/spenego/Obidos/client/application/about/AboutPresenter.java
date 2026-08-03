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

package com.spenego.Obidos.client.application.about;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListGroupItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
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
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.bootbox.Bootbox;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.BuildInfoService;
import com.spenego.Obidos.client.rpc.FernetService;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.BuildInfoDTO;
import com.spenego.Obidos.shared.dto.LimitedFernetDTO;

public class AboutPresenter extends Presenter<AboutPresenter.MyView, AboutPresenter.MyProxy> implements AboutUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	
	interface MyView extends View, HasUiHandlers<AboutUiHandlers>
	{
		public ObidosPanelHeader getPanelHeader();
	    public HTML getInfoHtml();
	    public HTML getBuildHtml();
	    public ObidosMessageRow getMessageRow();
	    ListGroupItem getUuserGuideLGI();
	    public ListGroupItem getAdminGuideLGI();
	    public Button getUserGuideButton();
	    public Button getAdminGuideButton();
	    public FormPanel getFormPanel();
	}

	@NameToken(NameTokens.ABOUT)
	@ProxyCodeSplit
    @UseGatekeeper(LoggedInGatekeeper.class)
	interface MyProxy extends ProxyPlace<AboutPresenter>
	{
	}
	
	private final PlaceManager placeManager;
	private final CurrentUser currentUser;
	@Inject
	AboutPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
	}

	protected void onReveal()
	{
		super.onReveal();
	}
	
	protected void onReset()
	{
		ClientUtils.setPanelHeaderColor(getView().getPanelHeader(), currentUser);
		showMessage(null);
		showObidosInfo();
		showBuildInfo();
		showGuideDownloadLink();
		Bootbox.hideAll();
		ClientUtils.addFormPaneUploadDownloadHandlers(getView().getFormPanel(), getView().getMessageRow(), glang.download());
		checkLicense();
	}
	
	protected void onHide()
	{
		super.onHide();
	}
	
	private void checkLicense()
	{
		ClientUtils.popLicenseWarningDialog(currentUser.getLoginResult().getLicenseStats());
	}

	private void showObidosInfo()
	{
	    HTML html = getView().getInfoHtml();
	    html.setHTML(glang.aboutObidos());
	}

	private void showBuildInfo()
	{
	    //HTML html = getView().getBuildHtml();
	    GwtAsyncWrapper<BuildInfoDTO> callback = new GwtAsyncWrapper<BuildInfoDTO>(this)
        {

            @Override
            public void uponFailure(Throwable e )
            {
                Window.alert("error: " + e.getMessage());
            }

            @Override
            public void uponSuccess(BuildInfoDTO dto)
            {
                showBuildInfo(dto);
            }
        };
        GWT.log("Calling build info service...");
        BuildInfoService.Utility.getInstance().getBuildInfo(callback);
	}

	@Override
	public void showChangeLogDialog()
	{
		// Bug #861. Display ChangeLog in a separate view
		ClientUtils.showPage(placeManager, NameTokens.CHANGELOG);
	}


	private void showBuildInfo(BuildInfoDTO dto)
	{
	    HTML html = getView().getBuildHtml();
	    gwtLog("Build time: " + dto.getBuildTime());

	    String headerString =
	    "<div class=\"row\">" + 
        "<div class=\"col-md-12 col-xs-12 col-centered\">" + 
        "<h4>Build Information</h4>" +
        "<ul class=\"list-group\">";

       String bList = "";
       String projectVersion = dto.getProjectVersion();

       String javaVersion = dto.getBuiltWithJavaVersion();
       // No need to disclose it's a Java application. print java version in hex
       String hexVersion = ClientUtils.getHexString(javaVersion);
       gwtLog("Java version: " + javaVersion);
       bList += "<li class=\"list-group-item list-group-item-info\">Version: " + projectVersion + ",  "+hexVersion;

       String schemaVersion = dto.getSchemaVersion();
       bList += "<span class=\"pull-right\">" + "Schema Version: " + schemaVersion + "</span></li>";

       String buildNumber = dto.getBuildNumber();
       bList += "<li class=\"list-group-item list-group-item-success\">Build Number: " + buildNumber;

       String buildTime = dto.getBuildTime();
       bList += "<span class=\"pull-right\">" + buildTime + "</span></li>";

       String footerString =
               "</ul>" +
               "</div>" + 
               "</div>";

       SafeHtml safeHtml = SafeHtmlUtils.fromSafeConstant(headerString + bList + footerString);
       html.setHTML(safeHtml);
	}

	private void showGuideDownloadLink()
	{
	    HTML html = getView().getBuildHtml();

	    String headerString =
	    "<div class=\"row\">" + 
        "<div class=\"col-md-12 col-xs-12 col-centered\">" + 
        "<h4>Build Information</h4>" +
        "<ul class=\"list-group\">";
	
	}
	private void gwtLog(String message)
	{
		ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
	}
	
	private void showMessage(String message)
	{
		getView().getMessageRow().showMessage(message);
	}
	
	private void showErrorMessage(String errorMessage)
	{
		getView().getMessageRow().showErrorMessage(errorMessage);
	}

	private void downloadWithPOSTGWT(String url)
	{
		FormPanel form = getView().getFormPanel();
		form.setMethod(FormPanel.METHOD_POST);
		form.setAction(url);
		form.submit();
	}
	
	private void downloadGuide(final int actionType)
	{
		GwtAsyncWrapper<LimitedFernetDTO> callback = new GwtAsyncWrapper<LimitedFernetDTO>(this)
		{

			@Override
			public void uponSuccess(LimitedFernetDTO dto)
			{
				StringBuilder sb = new StringBuilder(256);
				sb.append("token=")
				  .append(dto.getToken());
				String parameters = sb.toString();
				gwtLog("Parameters: " + parameters);

				gwtLog("Key: " + dto.getToken());
				String url = GWT.getHostPageBaseURL() + "download?" + parameters;
				gwtLog("XXX URL: " + url);
				downloadWithPOSTGWT(url);
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not get security token to download file: " + caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		Long documentId = 0L; // not used
		FernetService.Utility.getInstance().getFileDownloadFernetToken(authCreds, documentId, actionType, callback);
	}

	@Override
	public void downloadUserGuide()
	{
		downloadGuide(ObidosConstants.ACTION_TYPE_DOWNLOAD_USERGUIDE);
	}

	@Override
	public void downloadAdminGuide()
	{
		downloadGuide(ObidosConstants.ACTION_TYPE_DOWNLOAD_ADMINGUIDE);
	}

}
