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

package com.spenego.Obidos.client.application.installlicense;

import java.io.InvalidObjectException;
import java.util.Date;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;

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
import com.spenego.Obidos.client.application.widgets.ObidosButtonToolBar;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosTextArea;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.SystemConfigService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInAdminGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.LicenseStats;

public class InstallLicensePresenter extends Presenter<InstallLicensePresenter.MyView, InstallLicensePresenter.MyProxy>
		implements InstallLicenseUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	
	interface MyView extends View, HasUiHandlers<InstallLicenseUiHandlers>
	{
		public BlockQuote getHelpBlockQuote();
		public ObidosButtonToolBar getButtonToolBarBottom();
		public ObidosTextArea getLicenseTextArea();
		public ObidosMessageRow getMessageRow();
		public Button getSubmitButton();
	}


	@NameToken(NameTokens.INSTALL_LICENSE)
	@ProxyCodeSplit
    @UseGatekeeper(LoggedInAdminGatekeeper.class)
	interface MyProxy extends ProxyPlace<InstallLicensePresenter>
	{
	}
	
	private final PlaceManager placeManager;
	private final CurrentUser currentUser;

	@Inject
	InstallLicensePresenter(EventBus eventBus, MyView view, MyProxy proxy,
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

	protected void onHide()
	{
		super.onHide();
	}

	protected void onUnbind()
	{
		super.onUnbind();
	}

	protected void onReset()
	{
		super.onReset();
		showMessage(null);
		enableSubmitButton(false);
		clearTextArea();
		getView().getButtonToolBarBottom().adjustButtonsWidth();
	}
	
	private void enableSubmitButton(boolean enabled)
	{
		getView().getSubmitButton().setEnabled(enabled);
	}

	@Override
	public void install()
	{
		String license = getView().getLicenseTextArea().getValue();
		gwtLog("License: " + license);
		GwtAsyncWrapper<LicenseStats> callback = new GwtAsyncWrapper<LicenseStats>(this)
		{

			@Override
			public void uponSuccess(LicenseStats lstat)
			{
				enableSubmitButton(false);
				showMessage(glang.licenseInstalledSuccessfully(new Date().toString()));
				// update license
				currentUser.getLoginResult().setLicenseStats(lstat);

			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage(glang.failedToInstallLicense(caught.getMessage()));
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		SystemConfigService.Utility.getInstance().installLicense(authCreds, license, callback);
				
	}

	@Override
	public void help()
	{
		ClientUtils.showHelp(getView().getHelpBlockQuote());
	}

	@Override
	public void licensePasted()
	{
		enableSubmitButton(true);

		String text = getView().getLicenseTextArea().getValue();
		int len = text.length();
		String result = validateBase64(text, glang.invalidBase64Pasted());
		if (result != null && result.equals(glang.invalidBase64Pasted()))
		{
			invalidObidosLicensePasted();
		}
		else
		{
//			gwtLog(result);
			// do simple heuristics on the license string
			int score = ClientUtils.licenseScore(result);
			gwtLog("License score: " + score);
			if (score > 40)
			{
				// possibly valid, but we'll find out for sureafter submit
				getView().getLicenseTextArea().setEnabled(false);
				enableSubmitButton(true);
			}
			else
			{
				invalidObidosLicensePasted();
			}
			
		}
	}

	private void invalidObidosLicensePasted()
	{
		showErrorMessage(glang.invalidObidosLicense());
		getView().getLicenseTextArea().setEnabled(false);
		enableSubmitButton(false);
	}
	
	// Use native JavaScript base64 decode, send back the passed error
	// message if could not be decoded
	private native String validateBase64(String text, String errorMessage) /*-{
		try {
    		x = $wnd.atob(text);
    		return x;
		} catch(e) {
			return(errorMessage);
		}	
	}-*/;

	
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
	public void clearTextArea()
	{
		showMessage(null);
		getView().getLicenseTextArea().setValue(null);
		getView().getLicenseTextArea().setEnabled(true);
		enableSubmitButton(false);
	}
}
