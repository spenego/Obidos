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

package com.spenego.Obidos.client.application.licenseinfo;

import java.util.Date;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.html.Paragraph;

import com.google.gwt.user.client.ui.HTML;
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
import com.spenego.Obidos.client.application.widgets.ObidosRowBottom2px;
import com.spenego.Obidos.client.application.widgets.ObidosTextBox;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.SystemConfigService;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.client.security.LoggedInGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.LicenseKeyDTO;

public class LicenseInfoPresenter extends Presenter<LicenseInfoPresenter.MyView, LicenseInfoPresenter.MyProxy>
		implements LicenseInfoUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	
	interface MyView extends View, HasUiHandlers<LicenseInfoUiHandlers>
	{
		public Paragraph getLicenseTermsPara();
		public ObidosMessageRow getMessageRow();
		public TextBox getCompanyTextBox();
		public TextBox getEmailTextBox();
		public TextBox getExpiresTextBox();
		public TextBox getMaxUsersTextBox();
		public ObidosRowBottom2px getExpiresInRow();
		public TextBox getExpiresInDaysTextBox();
		public FormLabel getExpireLabel();
		public TextBox getSignedOnTextBox();
		public BlockQuote getHelpBlockQuote();
		public ObidosPanelHeader getPanelHeader();
		public ObidosTextBox getCustomerIdTextBox();
		public HTML getFeaturesHTML();
		public HTML getYourLicenseHtml();
	}

	@NameToken(NameTokens.LICENSE_INFO)
	@ProxyCodeSplit
    @UseGatekeeper(LoggedInGatekeeper.class)
	interface MyProxy extends ProxyPlace<LicenseInfoPresenter>
	{
	}
	
	final PlaceManager placeManager;
	final CurrentUser currentUser;

	@Inject
	LicenseInfoPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
		setPanelHeaderColor();
		displayLicense();
		showFeatures();
	}
	private void setPanelHeaderColor()
	{
		ObidosPanelHeader panelHeader = getView().getPanelHeader();
		ClientUtils.setPanelHeaderColor(panelHeader, currentUser);
	}

	@Override
	public void help()
	{
		ClientUtils.showHelp(getView().getHelpBlockQuote());
	}
	
	private void showMessage(String message)
	{
		getView().getMessageRow().showMessage(message);
	}
	
	private void showErrorMessgae(String errorMessage)
	{
		getView().getMessageRow().showErrorMessage(errorMessage);
	}

	private void gwtLog(String message)
	{
		ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
	}
	
	private void setExpireLabelColor(final String color, final String labelString)
	{
		FormLabel label = getView().getExpireLabel();
		label.setText(labelString);
		label.getElement().getStyle().setProperty("color", color);
	}
	
	private void displayLicense()
	{
		GwtAsyncWrapper<LicenseKeyDTO> callback = new GwtAsyncWrapper<LicenseKeyDTO>(this)
		{

			@Override
			public void uponSuccess(LicenseKeyDTO dto)
			{
				getView().getCompanyTextBox().setValue(dto.getCompanyName());
				getView().getEmailTextBox().setValue(dto.getCompanyEmail());
				setExpireLabelColor("#333", glang.expiresOn());
				
				Long signingEpoch = dto.getSigningEpoch();
				if (signingEpoch != null)
				{
					Date date = new Date(signingEpoch * 1000L);
					getView().getSignedOnTextBox().setValue(ClientUtils.formattedDate(date));
					
				}
				getView().getExpiresInRow().setVisible(false);
				if (dto.getExpirationEpoch() == null)
				{
					getView().getExpiresInRow().setVisible(false);
					getView().getExpiresTextBox().setValue(ObidosMessages.LANG.never());
				}
				else
				{
					long epochNow = ClientUtils.getUnixEpochSecond();
					long expiryEpoch = dto.getExpirationEpoch();
					gwtLog("Now epoch: " + epochNow);
					gwtLog("Expiry epoch: " + expiryEpoch);
					if (epochNow > expiryEpoch)
					{
						// license has expired
						setExpireLabelColor("#f00", glang.expiredOn());
					}
					else
					{
						getView().getExpiresInRow().setVisible(true);
						long seconds = (long)(expiryEpoch - epochNow);
						String message = ClientUtils.calculateTime(seconds);
						getView().getExpiresInDaysTextBox().setValue(message);
					}

					Date date = new Date(dto.getExpirationEpoch() * 1000L);
					getView().getExpiresTextBox().setValue(ClientUtils.formattedDate(date));
					
				}

				int maxUsers = ClientUtils.fromInteger(dto.getMaxUsers());
				String maxUsersStr = maxUsers == 0 ? glang.unlimited() : dto.getMaxUsers().toString();
				getView().getMaxUsersTextBox().setValue(maxUsersStr);

				gwtLog("terms: " + dto.getLicenseTerms());
				getView().getLicenseTermsPara().setHTML(dto.getLicenseTerms());

				if (dto.getCustomerId() == null)
				{
					dto.setCustomerId(glang.na());
				}
				gwtLog("Customer id: " + dto.getCustomerId());
				getView().getCustomerIdTextBox().setValue(dto.getCustomerId());
				
				HTML yourlicense = getView().getYourLicenseHtml();
				yourlicense.setHTML("");
				String licenseTerms = dto.getLicenseTerms();
				if (dto.getLicenseType() != null)
				{
					if (dto.getLicenseType().equals("Community")) //legacy
					{
						yourlicense.setHTML(glang.yourLicenseCommunity());
					}
					else if (dto.getLicenseType().equals("Opensource"))
					{
						yourlicense.setHTML(glang.yourLicenseOpensource());
					}
					else if (dto.getLicenseTerms().equals("Standard"))
					{
						yourlicense.setHTML(glang.yourLicenseStandard());
					}
					else if (dto.getLicenseTerms().equals("Enterprise"))
					{
						yourlicense.setHTML(glang.yourLicenseEnterprise());
					}
					else if (dto.getLicenseTerms().equals("Spenego Development"))
					{
						yourlicense.setHTML(glang.yourLicenseSapphireDev());
					}
					else
					{
						yourlicense.setHTML("");
					}
				}
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessgae("Could not retrieve license: " + caught.getMessage());
				
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		SystemConfigService.Utility.getInstance().getCurrentLicense(authCreds, callback);
	}
	
	private void showFeatures()
	{
		HTML html = getView().getFeaturesHTML();

		GwtAsyncWrapper<String> callback = new GwtAsyncWrapper<String>(this)
		{

			@Override
			public void uponSuccess(String result)
			{
				gwtLog(result);
				html.setHTML(result);
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessgae(caught.getMessage());
			}
	
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		SystemConfigService.Utility.getInstance().getFeaturesByLicense(authCreds, callback);

		
	}
}
