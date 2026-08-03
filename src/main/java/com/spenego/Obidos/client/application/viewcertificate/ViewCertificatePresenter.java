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

package com.spenego.Obidos.client.application.viewcertificate;

import java.util.List;

import org.gwtbootstrap3.client.ui.AnchorListItem;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.SystemConfigService;
import com.spenego.Obidos.client.security.LoggedInAdminGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;
import com.spenego.Obidos.shared.dto.CertificateInfoDTO;

public class ViewCertificatePresenter
		extends Presenter<ViewCertificatePresenter.MyView, ViewCertificatePresenter.MyProxy>
		implements ViewCertificateUiHandlers
{
	interface MyView extends View, HasUiHandlers<ViewCertificateUiHandlers>
	{
		public AnchorListItem getObidosCertsAli();
		public AnchorListItem getAdldapCertsAli();
		public ObidosMessageRow getMessageRow();
		public HTMLPanel getCertHtmlPanel();
		public HTML getCertHtml();
	}

	@NameToken(NameTokens.VIEW_CERTIFICATE)
	@ProxyCodeSplit
    @UseGatekeeper(LoggedInAdminGatekeeper.class)
	interface MyProxy extends ProxyPlace<ViewCertificatePresenter>
	{
	}

	@Inject
	ViewCertificatePresenter(EventBus eventBus, MyView view, MyProxy proxy)
	{
		super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);

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
		viewNginxCertificates();
		getView().getObidosCertsAli().setFocus(true);
	}

	@Override
	public void viewObidosCerts()
	{
		viewNginxCertificates();
	}

	@Override
	public void viewAdLdapCerts()
	{
		viewAdLdapCertificates();
	}
	
	private void viewAdLdapCertificates()
	{
		showMessage(null);
		getView().getCertHtml().setHTML("");
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
				getView().getCertHtml().setHTML(sb.toString());
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not obtain certificate info: "+ caught.getMessage());
				getView().getCertHtml().setHTML("");
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		SystemConfigService.Utility.getInstance().getAdLdapCertificateInfo(authCreds, callback);
	}

	private void viewNginxCertificates()
	{
		showMessage(null);
		getView().getCertHtml().setHTML("");
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
					boolean decode = false;
					certInfo = certInfo + ClientUtils.getCertificateInfo(dto, decode);
				}
				// add start and end table tags
				StringBuilder sb = new StringBuilder(32);
				sb.append("<table class=\"table\">");
				sb.append(certInfo);
				sb.append("</table>");
				getView().getCertHtml().setHTML(sb.toString());
			}

			@Override
			public void uponFailure(Throwable caught)
			{
				showErrorMessage("Could not obtain certificate info: "+ caught.getMessage());
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		SystemConfigService.Utility.getInstance().getNginxCertificateInfo(authCreds, callback);
	}
	
	private void showMessage(final String message)
	{
		getView().getMessageRow().showMessage(message);
	}
	
	private void showErrorMessage(final String errorMessage)
	{
		getView().getMessageRow().showErrorMessage(errorMessage);
	}
	
	private void gwtLog(final String message)
	{
		ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
	}
}