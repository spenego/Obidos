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

package com.spenego.Obidos.client.application.installcertificate;

import java.util.List;

import org.gwtbootstrap3.client.ui.BlockQuote;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.html.Paragraph;

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
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.application.widgets.ObidosRow;
import com.spenego.Obidos.client.application.widgets.ObidosTextArea;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.SystemConfigService;
import com.spenego.Obidos.client.security.LoggedInAdminGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.CertificateInfoDTO;

public class InstallCertificatePresenter
		extends Presenter<InstallCertificatePresenter.MyView, InstallCertificatePresenter.MyProxy>
		implements InstallCertificateUiHandlers
{
	private ObidosMessages glang = ObidosMessages.LANG;
	
	interface MyView extends View, HasUiHandlers<InstallCertificateUiHandlers>
	{
		public BlockQuote getHelpBlockQuote();
		public ObidosPanelHeader getPanelHeader();
		public ObidosTextArea getCertTextArea();
		public ObidosTextArea getKeyTextArea();
		public ObidosButtonToolBar getButtonToolBarBottom();
		public Button getSubmitButton();
		public Button getClearButton();
		public Button getViewButton();
		public ObidosMessageRow getMessageRow();
		public ObidosRow getKeyRow();
		public Paragraph getHelpParagraph();
	}

	@NameToken(NameTokens.INSTALL_CERTIFICATE)
	@ProxyCodeSplit
    @UseGatekeeper(LoggedInAdminGatekeeper.class)
	interface MyProxy extends ProxyPlace<InstallCertificatePresenter>
	{
	}
	
	final PlaceManager placeManager;

	@Inject
	InstallCertificatePresenter(EventBus eventBus, MyView view, MyProxy proxy,
			PlaceManager placeManager)
	{
		super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);

		
		this.placeManager = placeManager;
		
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
		updateForm();
		getView().getButtonToolBarBottom().adjustButtonsWidth();
	}
	
	private boolean isInstallingNginxCerts()
	{
		String type = ClientUtils.getTypeFromUrl(placeManager);
		if (ObidosConstants.INSTALL_NGINX_CERTS.equals(type))
		{
			return true;
		}
		return false;
	}
	
	private void updateForm()
	{
		getView().getCertTextArea().clear();
		getView().getKeyTextArea().clear();
		getView().getSubmitButton().setEnabled(false);
		getView().getViewButton().setEnabled(false);
		
		String type = ClientUtils.getTypeFromUrl(placeManager);
		if (ObidosConstants.INSTALL_NGINX_CERTS.equals(type))
		{
			getView().getPanelHeader().setText(glang.installObidosCertificate());
			getView().getKeyRow().setVisible(true);
			getView().getHelpParagraph().setHTML(glang.certificateHelp());
			getView().getSubmitButton().setText(glang.stage());
		}
		else if (ObidosConstants.INSTALL_ADLAP_CERTS.equals(type))
		{
			getView().getPanelHeader().setText(glang.installAdLdapCertificate());
			getView().getKeyRow().setVisible(false);
			getView().getHelpParagraph().setHTML(glang.adLdapCertificateHelp());
			getView().getSubmitButton().setText(glang.install());
		}
	}
	
	private String getPanelTitle()
	{
		/*
		String type = ClientUtils.getTypeFromUrl(placeManager);
		if (ObidosConstants.INSTALL_NGINX_CERTS.equals(type))
		{
			return glang.viewObidosCertificate();
		}
		else if (ObidosConstants.INSTALL_ADLAP_CERTS.equals(type))
		{
			return glang.viewAdLdapCertificate();
		}
		return glang.viewObidosCertificate();
		*/
		return glang.decodeCertificates();
	}

	@Override
	public void help()
	{
		ClientUtils.showHelp(getView().getHelpBlockQuote());
	}
	
	private void showMessage(final String message)
	{
		getView().getMessageRow().showMessage(message);
	}
	
	private void showErrorMessage(final String errorMessage)
	{
		getView().getMessageRow().showErrorMessage(errorMessage);
	}

	private void installNginxCerts()
	{
		String certs = getView().getCertTextArea().getValue();
		if (certs == null || certs.length() == 0)
		{
			showErrorMessage("Please paste PEM encoded Certificates");
			return;
		}
		
		String key = getView().getKeyTextArea().getValue();
		if (key == null || key.length() == 0)
		{
			showErrorMessage("Please paste PEM encoded Private key");
			return;
		}
		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				updateForm();
				ClientUtils.showBootboxDialog("Certificates staged", glang.certificateStaged());
				showMessage(glang.certificateStaged());
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage(caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		SystemConfigService.Utility.getInstance().installNginxCertificates(authCreds, certs, key, callback);
	}

	private void installAdLdapCerts()
	{
		String pemCerts = getView().getCertTextArea().getValue();
		if (pemCerts == null || pemCerts.length() == 0)
		{
			showErrorMessage("Please paste PEM encoded Certificates");
			return;
		}
		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{
			@Override
			public void uponSuccess(Void result)
			{
				updateForm();
				showMessage("AD/LDAP certificates installed successfully");
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage(caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		SystemConfigService.Utility.getInstance().installAdLdapCertificates(authCreds, pemCerts, callback);
	}

	@Override
	public void clear()
	{
		showMessage("");
		getView().getCertTextArea().clear();
		getView().getKeyTextArea().clear();
		getView().getSubmitButton().setEnabled(false);
		getView().getViewButton().setEnabled(false);
		
	}

	@Override
	public void viewCertificates()
	{
		String pemChain = getView().getCertTextArea().getValue();
		if (pemChain == null || pemChain.length() == 0)
		{
			showErrorMessage("Please paste PEM formated certificates");
			return;
		}
		showMessage(null);
		GwtAsyncWrapper<List<CertificateInfoDTO>> callback = new GwtAsyncWrapper<List<CertificateInfoDTO>>(this)
		{

			@Override
			public void uponSuccess(List<CertificateInfoDTO> certDTOs)
			{
				gwtLog("List size: " + certDTOs.size());
				
				String certInfo = "";
				for (CertificateInfoDTO dto : certDTOs)
				{
					gwtLog(dto.getSubjectCommonName());
					// get the table body with all the certificates
					boolean decode = true;
					certInfo = certInfo + ClientUtils.getCertificateInfo(dto, decode);
				}
				// add start and end table tags
				StringBuilder sb = new StringBuilder(32);
				sb.append("<table class=\"table\">");
				sb.append(certInfo);
				sb.append("</table>");
				String title = getPanelTitle();
				ClientUtils.showBootboxDialog(title, sb.toString());
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not obtain certificate info: "+ caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		SystemConfigService.Utility.getInstance().getCertificateInfo(authCreds, pemChain, callback);
	}
	
	private void gwtLog(final String message)
	{
		ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
	}

	@Override
	public void certsPastedCallback()
	{
		ObidosTextArea textArea = getView().getCertTextArea();
		boolean rc1 = ClientUtils.pemCertificateMarkersFound(getView().getCertTextArea().getValue());
		gwtLog("rc1: " + rc1);
		if (rc1)
		{
			getView().getViewButton().setEnabled(true);
		}

		if (!isInstallingNginxCerts() && rc1)
		{
			getView().getSubmitButton().setEnabled(true);
			getView().getClearButton().setEnabled(true);
		}

		boolean rc2 = ClientUtils.pemPrivateKeyMarkers(getView().getKeyTextArea().getValue());
		if (rc1 && rc2)
		{
			getView().getSubmitButton().setEnabled(true);
			getView().getClearButton().setEnabled(true);
		}
	}

	@Override
	public void keyPastedCallback()
	{
		boolean rc1 = ClientUtils.pemPrivateKeyMarkers(getView().getKeyTextArea().getValue());
		

		boolean rc2 = ClientUtils.pemCertificateMarkersFound(getView().getCertTextArea().getValue());
		if (rc1 && rc2)
		{
			getView().getSubmitButton().setEnabled(true);
			getView().getClearButton().setEnabled(true);
		}
	}

	@Override
	public void installCerts()
	{
		if (isInstallingNginxCerts())
		{
			installNginxCerts();
		}
		else
		{
			installAdLdapCerts();
		}
	}
}