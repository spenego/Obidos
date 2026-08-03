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

package com.spenego.Obidos.client.application.sendresetpassphraserequest;

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
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.spenego.Obidos.client.application.ApplicationPresenter;
import com.spenego.Obidos.client.application.widgets.ObidosMessageRow;
import com.spenego.Obidos.client.application.widgets.ObidosPanelHeader;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.place.NameTokens;
import com.spenego.Obidos.client.rpc.GwtAsyncWrapper;
import com.spenego.Obidos.client.rpc.UserService;
import com.spenego.Obidos.client.security.LoggedInUserLicenseGatekeeper;
import com.spenego.Obidos.client.util.ClientUtils;
import com.spenego.Obidos.shared.dto.AuthCredsDTO;

public class SendResetPassphraseRequestPresenter
		extends Presenter<SendResetPassphraseRequestPresenter.MyView, SendResetPassphraseRequestPresenter.MyProxy>
		implements SendResetPassphraseRequestUiHandlers
{
	interface MyView extends View, HasUiHandlers<SendResetPassphraseRequestUiHandlers>
	{
		public BlockQuote getHelpBlockQuote();
		public Button getSendEmailButton();
		public ObidosPanelHeader getPanelHeader();
		public ObidosMessageRow getMessageRow();

	}

	@NameToken(NameTokens.SEND_RESET_PASSPHRASE_REQUEST)
	@ProxyCodeSplit
	@UseGatekeeper(LoggedInUserLicenseGatekeeper.class)
	interface MyProxy extends ProxyPlace<SendResetPassphraseRequestPresenter>
	{
	}

	@Inject
	SendResetPassphraseRequestPresenter(EventBus eventBus, MyView view, MyProxy proxy)
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
		showMessage("");
		enableSendEmailButton(true);
	}
	
	private void enableSendEmailButton(boolean enabled)
	{
		getView().getSendEmailButton().setEnabled(enabled);
	}

	@Override
	public void help()
	{
		ClientUtils.showHelp(getView().getHelpBlockQuote());
	}

	@Override
	public void sendEmail()
	{
		GwtAsyncWrapper<Void> callback = new GwtAsyncWrapper<Void>(this)
		{

			@Override
			public void uponSuccess(Void result)
			{
				showMessage(ObidosMessages.LANG.passphraseResetInstructionWillbeSent());
				enableSendEmailButton(false);
			}

			@Override
			public void uponFailure(Throwable caught)
			{
//				showErrorMessage("Could not send Passphrase reset instructions: "+ caught.getMessage());
				// be quiet, just log
				String errorMessage = ObidosMessages.LANG.couldNotSendPassphraseResetInstructions(caught.getMessage());
				gwtLog(errorMessage);
				showErrorMessage(errorMessage);
			}
		};
		AuthCredsDTO authCreds = ClientUtils.getAuthCreds();
		UserService.Utility.getInstance().sendPassphraseResetEmail(authCreds, callback);
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
}