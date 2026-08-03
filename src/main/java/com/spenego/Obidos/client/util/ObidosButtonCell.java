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

package com.spenego.Obidos.client.util;

import java.util.function.Function;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.spenego.Obidos.client.i18n.ObidosMessages;
import com.spenego.Obidos.client.security.CurrentUser;
import com.spenego.Obidos.shared.ObidosConstants;
import com.spenego.Obidos.shared.dto.AuditDTO;
import com.spenego.Obidos.shared.dto.ContainerDTO;
import com.spenego.Obidos.shared.dto.GroupDTO;
import com.spenego.Obidos.shared.dto.LicenseStats;
import com.spenego.Obidos.shared.dto.LimitedItemDTO;
import com.spenego.Obidos.shared.dto.LimitedUserDTO;
import com.spenego.Obidos.shared.dto.LimitedUserForAdminDTO;
import com.spenego.Obidos.shared.dto.NotificationDTO;
import com.spenego.Obidos.shared.dto.PermissionDTO;
import com.spenego.Obidos.shared.dto.SharedContainerDTO;
import com.spenego.Obidos.shared.dto.SharedItemDTO;
import com.spenego.Obidos.shared.dto.UserDTO;
import com.spenego.Obidos.shared.dto.UserDefinedTypeDTO;
import com.spenego.Obidos.shared.dto.UserGroupComboDTO;

/**
 * A custom cell for all link type buttons.
 * 
 * @author spgdev@spenego.com - Dec 25, 2018
 */
public class ObidosButtonCell extends AbstractCell<String>
{
	
	private int cellType;
	private LimitedItemDTO 			limitedItemDTO;
	private SharedContainerDTO 		sharedContainerDTO;
	private ContainerDTO 			containerDTO;
	private SharedItemDTO 			sharedItemDTO;
	private NotificationDTO 		notificationDTO;
	private AuditDTO 				auditDTO;
	private LimitedUserDTO 			limitedUserDTO;
	private LimitedUserForAdminDTO 	limitedUserForAdminDTO;
	private UserGroupComboDTO 		userGroupComboDTO;
	private UserDefinedTypeDTO 		userDefinedTypeDTO;
	private CurrentUser 			currentUser;
	private GroupDTO 				groupDTO;
	// private String fgColor;
	// private String bgColor;
	// private String title;
	// private String toolTip;
	private Function<Object, String> valueFunction;
	private Function<Object, String> linkFunction;
	// private boolean isNote = false;
	private ObidosMessages lang = ObidosMessages.LANG;

	public ObidosButtonCell(int cellType)
	{
		super("click");
		this.cellType = cellType;
		// this.fgColor = "#000000";
		// this.bgColor = "#ffffff";
	}

	public ObidosButtonCell(int cellType, boolean isNote)
	{
		super("click");
		this.cellType = cellType;
		// this.isNote = isNote;
	}

	public ObidosButtonCell(int cellType, Function<Object, String> valueFunction, Function<Object, String> linkFunction)
	{
		this(cellType);
		this.valueFunction = valueFunction;
		this.linkFunction = linkFunction;
	}

	public ObidosButtonCell(int cellType, CurrentUser currentUser)
	{
		super("click");
		this.cellType = cellType;
		this.currentUser = currentUser;
	}

	public ObidosButtonCell(int cellType, String title, String fgColor, String bgColor)
	{
		// this.fgColor = fgColor;
		// this.bgColor = bgColor;
	}
	
	public void render(Context context, String value, SafeHtmlBuilder sb)
	{
		if (value == null)
		{
			return;
		}

		if (context.getKey() == null)
		{
			return;
		}
		String htmlButton = null;
		Object contextKey = context.getKey();
		String cn = contextKey.getClass().getSimpleName();
		// removing instance of code, it creates a mess if parent class is
		// tested first.
		if (cn.equals(SharedContainerDTO.class.getSimpleName()))
		{
			sharedContainerDTO = (SharedContainerDTO) contextKey;
		}
		else if (cn.equals(ContainerDTO.class.getSimpleName()))
		{
			containerDTO = (ContainerDTO) contextKey;
		}
		else if (cn.equals(LimitedItemDTO.class.getSimpleName()))
		{
			limitedItemDTO = (LimitedItemDTO) contextKey;
		}
		else if (cn.equals(SharedItemDTO.class.getSimpleName()))
		{
			sharedItemDTO = (SharedItemDTO) contextKey;
		}
		else if (cn.equals(NotificationDTO.class.getSimpleName()))
		{
			notificationDTO = (NotificationDTO) contextKey;
		}
		else if (cn.equals(LimitedUserDTO.class.getSimpleName()))
		{
			limitedUserDTO = (LimitedUserDTO) contextKey;
		}
		else if (cn.equals(LimitedUserForAdminDTO.class.getSimpleName()))
		{
			limitedUserForAdminDTO = (LimitedUserForAdminDTO) contextKey;
		}
		else if (cn.equals(UserGroupComboDTO.class.getSimpleName()))
		{
			userGroupComboDTO = (UserGroupComboDTO) contextKey;
		}
		else if (cn.equals(UserDefinedTypeDTO.class.getSimpleName()))
		{
			userDefinedTypeDTO = (UserDefinedTypeDTO) contextKey;
		}
		else if (cn.equals(GroupDTO.class.getSimpleName()))
		{
			groupDTO = (GroupDTO) contextKey;
		}
		else if (cn.equals(AuditDTO.class.getSimpleName()))
		{
			auditDTO = (AuditDTO) contextKey;
		}

		switch (this.cellType)
		{
		case ObidosConstants.CELL_TYPE_GENERAL:
			htmlButton = (contextKey != null)
					? ClientUtils.makeNonClickableButton(valueFunction.apply(contextKey),
							linkFunction.apply(contextKey))
					: ClientUtils.makeDisabledText(lang.unknown(), lang.unknown());
			break;

		// Container Not Shared or Shared
		case ObidosConstants.CELL_TYPE_CONTAINER_SHARED_NO_TOOLTIP:
		{
			if (sharedContainerDTO != null)
			{
				boolean shared = ClientUtils.isContainerSharedWithSomeone(sharedContainerDTO);
				if (shared)
				{
					String text = ObidosMessages.LANG.sharedContainer();
					htmlButton = ClientUtils.makeSharedDisabledTextButton(text, null);
				} else
				{
					String text = ObidosMessages.LANG.notShared();
					htmlButton = ClientUtils.makeNotSharedDisabledTextButton(text, null);
				}
			} else
			{
				htmlButton = ClientUtils.makeDisabledText("Shared Unknown", "Unknwon DTO Passed in ObidosButtonCell");
			}
			break;
		}

		// item shareable or private
		case ObidosConstants.CELL_TYPE_ITEM_SHAREABLE_OR_PRIVATE:
		{
			if (limitedItemDTO != null)
			{
				boolean shared = !ClientUtils.isItemPrivate(limitedItemDTO);
				if (shared)
				{
					String text = ObidosMessages.LANG.shareable();
					htmlButton = ClientUtils.makeShareableDisabledTextButton(text, null);
				} else
				{
					String text = ObidosMessages.LANG.privateX();
					htmlButton = ClientUtils.makePrivateDisabledTextButton(text, null);
				}
			}
			else
			{
				htmlButton = ClientUtils.makeDisabledText("Shared Unknown", "Unknwon DTO Passed in ObidosButtonCell");
			}
			break;
		}

		case ObidosConstants.CELL_TYPE_CONTAINER_NAME:
		{
			if (sharedContainerDTO != null || containerDTO != null)
			{
				ContainerDTO dto = sharedContainerDTO != null ? sharedContainerDTO : containerDTO;
				String title = dto.getName();
				String tooltip = lang.listItemsInContainer();
				boolean shared = ClientUtils.isContainerSharedWithSomeone(dto);
				if (shared)
				{				
					String iconClass = "fa fa-share-square";
					String iconColor =  null;
					tooltip = lang.listItemsInSharedContainer();
					htmlButton = ClientUtils.makeLinkButton(title, iconClass, iconColor, tooltip);
				}
				else
				{
					htmlButton = ClientUtils.makeListItemsInContainerButton(title, tooltip);
				}
			}
			else if (sharedItemDTO != null)
			{
				String title = sharedItemDTO.getContainerName();
				String tooltip = "";
				htmlButton = ClientUtils.makeListItemsInContainerButton(title, tooltip);
			}
			break;
		}
		
		case ObidosConstants.CELL_TYPE_TAKE_CONTAINER_OWNERSHIP:
		{
			if (sharedContainerDTO != null)
			{
				boolean canModify = ClientUtils.fromBoolean(sharedContainerDTO.getModify());
				gwtLog("NNN canModify: " + canModify);
				boolean canOwn = ClientUtils.fromBoolean(sharedContainerDTO.getOwnershipControl());
				gwtLog("Can own container? " + canOwn);
				String title = lang.takeOwnership();
				String tooltip = lang.takeOwnership();
				if (canOwn)
				{
					String iconClass = "fa fa-user";
					String iconColor = null;
					htmlButton = ClientUtils.makeLinkButton(title, iconClass, iconColor, tooltip);
				}
				else
				{
					htmlButton = ClientUtils.makeDisabledTextButton(lang.takeOwnership(), lang.noPermissionToTakeOwnership());
				}
			}
			else
			{
				htmlButton = ClientUtils.makeDisabledTextButton(lang.takeOwnership(), lang.takeOwnership());
			}
			break;
		}

		case ObidosConstants.CELL_TYPE_CONTAINER_NAME_IN_LIST_ITEMS:
		{
			if (limitedItemDTO != null)
			{
				String containerName = limitedItemDTO.getContainerName();
				htmlButton = ClientUtils.makeListItemsInContainerButton(containerName,lang.listItemsInContainer());
			}
			break;
		}

		case ObidosConstants.CELL_TYPE_TEMPLATE_NAME:
		{
			if (userDefinedTypeDTO != null)
			{
				String title = userDefinedTypeDTO.getName();
				String tooltip = null;
				String iconClass = null;
				String iconColor = null;
				htmlButton = ClientUtils.makeLinkButton(title, iconClass, iconColor, tooltip);
			}
			break;
		}

		case ObidosConstants.CELL_TYPE_ITEM_NAME:
		case ObidosConstants.CELL_TYPE_NOTE_NAME:
		{
			if (limitedItemDTO != null)
			{
				boolean hasExpiration = false;
				boolean hasExpired = limitedItemDTO.isShareExpired();
				boolean shared = ClientUtils.fromBoolean(limitedItemDTO.getShared());

				String tooltip = limitedItemDTO.getName();
				if (limitedItemDTO.getItemExpiration() != null)
				{
					hasExpiration = true;
					tooltip = ClientUtils.getShareMsg(limitedItemDTO);
				}
				String title = limitedItemDTO.getName();
				htmlButton = ClientUtils.makeViewItemButton(title, shared, hasExpiration, hasExpired, tooltip);
			}
			else if (sharedItemDTO != null)
			{
				Long documentId = ClientUtils.getDocumentId(sharedItemDTO);
				gwtLog("Document id: " + documentId);
						
				String title = sharedItemDTO.getName();
				boolean shared = true;
				boolean hasExpiration = false;
				boolean hasExpired = false;
				String tooltip = lang.viewSharedItem();
				if (this.cellType == ObidosConstants.CELL_TYPE_ITEM_NAME)
				{
					tooltip = lang.viewItem();
					if (shared)
					{
						tooltip = lang.viewSharedItem();
					}
				}
				else
				{
					tooltip = lang.viewNote();
					if (shared)
					{
						tooltip = lang.viewSharedNote();
					}
				}

				htmlButton = ClientUtils.makeViewItemButton(title, shared, hasExpiration, hasExpired, tooltip);
			}
			break;
		}
		case ObidosConstants.CELL_TYPE_GROUP:
		{
			if (groupDTO != null)
			{
				String title = groupDTO.getName();
				String iconClass = "fa fa-group";
				String iconColor = null;
				String tooltip = lang.clickToListUsers();
				htmlButton = ClientUtils.makeLinkButton(title, iconClass, iconColor, tooltip);
			}
			break;
		}
		
		case ObidosConstants.CELL_TYPE_EMAIL:
		{
			String iconClass = "fa fa-envelope";
			String iconColor = null;
			String tooltip = lang.clickToSendEmail();

			if (limitedUserDTO != null)
			{
				String title = limitedUserDTO.getEmail1();
				htmlButton = ClientUtils.makeMailtoButton(title, iconClass, iconColor, tooltip);
			}
			else if (userGroupComboDTO != null)
			{
				if (userGroupComboDTO.isUser())
				{
					String title = userGroupComboDTO.getEmail1();
					htmlButton = ClientUtils.makeMailtoButton(title, iconClass, iconColor, tooltip);
				}
				else
				{
					htmlButton = lang.na();
				}
			}
			else if (limitedUserForAdminDTO != null)
			{
				String title = limitedUserForAdminDTO.getEmail1();
				htmlButton = ClientUtils.makeMailtoButton(title, iconClass, iconColor, tooltip);
			}
			break;
		}

		case ObidosConstants.CELL_TYPE_FULLNAME:
		{
			String title = null;
			String iconClass = "fa fa-user";
			String iconColor = null;
			String tooltip = "";
			boolean uploadedProfilePic = false;
			if (limitedUserForAdminDTO != null)
			{
				gwtLog("Getting fullname.......");
				title = limitedUserForAdminDTO.getFullname();
				gwtLog("Getting fullname back: " + title);
				tooltip = lang.clickToSeeUserInfo();
				uploadedProfilePic = ClientUtils.fromBoolean(limitedUserForAdminDTO.getProfilePictureEnabled());
				gwtLog("XX Uploaded profile pic: " + uploadedProfilePic);
			}
			else if (limitedUserDTO != null)
			{
				title = limitedUserDTO.getFullname();
				tooltip = lang.clickToSeeUserInfo();
				uploadedProfilePic = ClientUtils.fromBoolean(limitedUserDTO.getProfilePictureEnabled());
				if (limitedUserDTO.getProfilePictureEnabled())
				{
					gwtLog("XX XX User: " + limitedUserDTO.getFullname() + " pic enabled");
				}
				else
				{
					gwtLog("XX XX User: " + limitedUserDTO.getFullname() + " pic false");
				}
				gwtLog("XX User: " + limitedUserDTO.getFullname() + " uploadedProfilePic: " + uploadedProfilePic);
				if (uploadedProfilePic)
				{
					gwtLog("XX User: " + limitedUserDTO.getFullname() + " uploadedProfilePic: " + uploadedProfilePic);
				}
			}
			else if (sharedItemDTO != null)
			{
				title = sharedItemDTO.getOwnerFullname();
				tooltip = lang.clickToSeeOwnerInfo();
				uploadedProfilePic = ClientUtils.fromBoolean(sharedItemDTO.getProfilePictureEnabled());
				gwtLog("sharedItemDTO upload profile pic: " + uploadedProfilePic);
			}
			else if (sharedContainerDTO != null)
			{
				title = sharedContainerDTO.getOwnerFullname();
				tooltip = lang.clickToSeeOwnerInfo();
				uploadedProfilePic = ClientUtils.fromBoolean(sharedContainerDTO.getProfilePictureEnabled());
			}
			else if (userGroupComboDTO != null) // list users container is shared with view
			{
				title = userGroupComboDTO.getName();
				if (userGroupComboDTO.isGroup())
				{
					gwtLog("xyz Group id: "+ userGroupComboDTO.getId());
				}
				else
				{
					gwtLog("xyz User id: "+ userGroupComboDTO.getId());
				}
				if (userGroupComboDTO.isGroup())
				{
					iconClass = "fa fa-group";
					tooltip = lang.clickToListUsers();
				}
				else
				{
					tooltip = lang.clickToSeeUserInfo();
				}
				uploadedProfilePic = ClientUtils.fromBoolean(userGroupComboDTO.getProfilePictureEnabled());
			}
			if (uploadedProfilePic)
			{
				iconColor = ObidosConstants.PURPLE_COLOR;
			}

			if (title != null)
			{
				htmlButton = ClientUtils.makeLinkButton(title, iconClass, iconColor, tooltip);
			}
			break;
		}

		case ObidosConstants.CELL_TYPE_CONTAINER_SHARED_WITH_USERS_GROUPS:
		{
			if (containerDTO != null)
			{
				boolean shared = ClientUtils.isContainerSharedWithSomeone(containerDTO);
				if (shared)
				{
					htmlButton = ClientUtils.makeSharedWithUsersTextbutton(lang.list(),
							lang.listUsersContainerIsSharedWith());

				} else
				{
					String text = lang.none();
					htmlButton = ClientUtils.makeNotSharedDisabledTextButton(text,
							lang.containerIsNotSharedWithAnyone());
				}

			}
			break;
		}

		case ObidosConstants.CELL_TYPE_ITEM_SHARED_WITH_USERS_GROUPS:
		case ObidosConstants.CELL_TYPE_NOTE_SHARED_WITH_USERS_GROUPS:
		{
			if (limitedItemDTO != null)
			{
				boolean shared = ClientUtils.fromBoolean(limitedItemDTO.getShared());
				if (shared)
				{
					String tooltip = lang.listUsersItemIsSharedWith();
					if (this.cellType == ObidosConstants.CELL_TYPE_NOTE_SHARED_WITH_USERS_GROUPS)
					{
						tooltip = lang.listUsersGroupsNoteIsSharedWith();
						
					}
					htmlButton = ClientUtils.makeSharedWithUsersTextbutton(lang.list(),tooltip);
				} else
				{
					String text = lang.none();
					htmlButton = ClientUtils.makeNotSharedDisabledTextButton(text, lang.itemIsNotSharedWithAnyone());
				}
			}
			break;
		}


		case ObidosConstants.CELL_TYPE_USERS:
		{
			String title = lang.list();
			String tooltip = lang.listUsersInGroup();
			htmlButton = ClientUtils.makeUsersTextbutton(title, tooltip);
			break;
		}

		case ObidosConstants.CELL_TYPE_LOCKED_USER:
		{
			htmlButton = ClientUtils.makeDisabledText(lang.unknown(), lang.unknown());
			if (limitedUserForAdminDTO != null)
			{
				String title = limitedUserForAdminDTO.getUsername();
				String tooltip = lang.userIsLocked(limitedUserForAdminDTO.getFullname());
				htmlButton = ClientUtils.makeLockedUserButton(title, tooltip);
			}
			break;
		}

		case ObidosConstants.CELL_TYPE_MARKED_DELETED_USER:
		{
			htmlButton = ClientUtils.makeDisabledText(lang.unknown(), lang.unknown());
			if (limitedUserForAdminDTO != null)
			{
				String title = limitedUserForAdminDTO.getUsername();
				String tooltip = lang.userIsMarkedDeleted(limitedUserForAdminDTO.getFullname());
				htmlButton = ClientUtils.makeDeletedUserButton(title, tooltip);
			}
			break;
		}

		// Container is Private or Shareable
		case ObidosConstants.CELL_TYPE_CONTAINER_SHAREABLE_NO_TOOLTIP:
		{
			if (containerDTO != null)
			{
				if (ClientUtils.isContainerPrivate(containerDTO))
				{
					String text = ObidosMessages.LANG.privateContainer();
					htmlButton = ClientUtils.makePrivateDisabledTextButton(text, null);
				} else
				{
					String text = ObidosMessages.LANG.shareableContainer();
					htmlButton = ClientUtils.makeShareableDisabledTextButton(text, null);
				}
			} else
			{
				htmlButton = ClientUtils.makeDisabledText("Shareable Unknown",
						"Unknwon DTO Passed in ObidosButtonCell");
			}
			break;
		}

		case ObidosConstants.CELL_TYPE_CONTAINER_USER_GROUP:
		case ObidosConstants.CELL_TYPE_ITEM_USER_GROUP:
		{
			if (userGroupComboDTO != null)
			{
				if (userGroupComboDTO.isUser())
				{
					htmlButton = ClientUtils.makeUserTextButton();
					
				}
				else if (userGroupComboDTO.isGroup())
				{
					htmlButton = ClientUtils.makeGroupTextButton();
				}
			}
			break;
		}
		
		case ObidosConstants.CELL_TYPE_USERS_IN_GROUP:
		{
			if (userGroupComboDTO != null)
			{
				String title = userGroupComboDTO.getName();
				String iconClass = null;
				String iconColor = null;
				String tooltip = "";
				if (userGroupComboDTO.isGroup())
				{
					tooltip = lang.listUsersInGroup();
				}
				else
				{
					tooltip = lang.viewUserInfo();
				}
				htmlButton = ClientUtils.makeLinkButton(title, iconClass, iconColor, tooltip);

			}
			break;
		}
		

		// Container/item shared with others
		case ObidosConstants.CELL_TYPE_SHARED_WITH_OTHERS:
		{
			if (containerDTO != null)
			{
				Boolean shared = containerDTO.getShared();
				boolean isShared = false;
				if (shared != null)
				{
					isShared = shared.booleanValue();
				}
				if (isShared)
				{
					String text = value;
					String tooltip = ObidosMessages.LANG.containerSharedWithOthers();
					htmlButton = ClientUtils.makeSharedWithOthersTextButton(text, tooltip);
				} else
				{
					htmlButton = "<span title=\"" + value + "\"</span>" + value;
				}
			}
			if (limitedItemDTO != null)
			{
				boolean shared = ClientUtils.fromBoolean(limitedItemDTO.getShared());
				if (shared)
				{
					// String text = value;
					// String tooltip =
					// ObidosMessages.LANG.itemSharedWithUsers();
					// if (isNote)
					// {
					// tooltip = ObidosMessages.LANG.noteSharedWithOthers();
					// }

					htmlButton = ClientUtils.getSharedItemSpan(limitedItemDTO, value);
				} else
				{
					htmlButton = "<span title=\"" + value + "\"</span>" + value;
				}
			}
			break;
		}

		case ObidosConstants.CELL_TYPE_SHARE:
		{
			htmlButton = ClientUtils.makeShareButton();

			// Container
			if (containerDTO != null)
			{
				// check if license allows it
				if (! doesLicenseAllowContainerSharing())
				{
					String tooltip = lang.notSupportedByLicense();
					htmlButton = ClientUtils.makeDisabledShareButton(tooltip);
				}

				Boolean isPrivate = ClientUtils.isContainerPrivate(containerDTO);
				if (isPrivate)
				{
					String text = ObidosMessages.LANG.privateX();
					String tooltip = ObidosMessages.LANG.privateContainersCannotbeShared();
					if (this.cellType == ObidosConstants.CELL_TYPE_SHARE)
					{
						htmlButton = ClientUtils.makePrivateDisabledTextButton(text, tooltip);
					}
					else
					{
						htmlButton = ClientUtils.makePrivateDisabledTextButton(text, null);
					}
				}
			} else if (limitedItemDTO != null)
			{
				Boolean shareable = limitedItemDTO.getShareable();
				// I noticed this
				if (shareable == null)
				{
					shareable = false;
				}
				if (!shareable)
				{
					String text = ObidosMessages.LANG.privateX();
					String tooltip = ObidosMessages.LANG.privateItemCannotBeShared();
					htmlButton = ClientUtils.makePrivateDisabledTextButton(text, tooltip);
				}
			} else
			{
				htmlButton = ClientUtils.makeDisabledText("Unknown", "Unknwon DTO Passed in ObidosButtonCell");
			}
			break;
		}
		case ObidosConstants.CELL_TYPE_REVOKE:
		{
			htmlButton = ClientUtils.makeRevokeButton();

			Boolean isShared = false;
			// looks like now it is container DTO Jul-31-2022
			/*
			if (sharedContainerDTO != null)
			{
				gwtLog("XX: sharedContainerDTO is not null");
				if (sharedContainerDTO.getShared() || sharedContainerDTO.getGroupShared())
				{
					isShared = true;
				}
				if (!isShared)
				{
					String text = ObidosMessages.LANG.notShared();
					String tooltip = ObidosMessages.LANG.onlySharedContainerCanBeRevoked();
					htmlButton = ClientUtils.makeNotSharedDisabledTextButton(text, tooltip);
				}
			} 
			*/
			if (containerDTO != null)
			{
				if (containerDTO.getShared() || containerDTO.getGroupShared())
				{
					isShared = true;
				}
				if (!isShared)
				{
					String text = ObidosMessages.LANG.notShared();
					String tooltip = ObidosMessages.LANG.onlySharedContainerCanBeRevoked();
					htmlButton = ClientUtils.makeNotSharedDisabledTextButton(text, tooltip);
				}
			}
			else if (limitedItemDTO != null)
			{
				if (limitedItemDTO.getShared())
				{
					isShared = true;
				}
				if (!isShared)
				{
					String text = ObidosMessages.LANG.notShared();
					String tooltip = ObidosMessages.LANG.onlySharedItemCanBeRevoked();
					htmlButton = ClientUtils.makeNotSharedDisabledTextButton(text, tooltip);
				}
			} else
			{
				gwtLog("XX fdup");
				htmlButton = ClientUtils.makeDisabledText("Revoke Unknown", "Unknwon DTO Passed in ObidosButtonCell");
			}
			break;
		}
		case ObidosConstants.CELL_TYPE_ADD:
		{
			if (sharedContainerDTO != null)
			{
				// Bug# 131, Add permission was missing
				boolean mayAdd = ClientUtils.fromBoolean(sharedContainerDTO.getAddPermitted());
						
				if (mayAdd)
				{
					htmlButton = ClientUtils.makeAddButton();
				}
				else
				{
					String text = ObidosMessages.LANG.add();
					String tooltip = "No Add Permission";
					htmlButton = ClientUtils.makeNotSharedDisabledTextButton(text, tooltip);
				}
			}
			else
			{
				htmlButton = ClientUtils.makeAddButton();
			}
			break;
		}
		case ObidosConstants.CELL_TYPE_DELETE:
		{
			htmlButton = ClientUtils.makeDeleteButton();
			break;
		}

		case ObidosConstants.CELL_TYPE_REMOVE:
		{
			htmlButton = ClientUtils.makeRemoveBtton();
			break;
		}

		case ObidosConstants.CELL_TYPE_EDIT:
		{
			htmlButton = ClientUtils.makeEditButton();
			break;
		}

		case ObidosConstants.CELL_TYPE_TEST:
		{
			htmlButton = ClientUtils.makeTestButton();
			break;
		}

		case ObidosConstants.CELL_TYPE_EDIT_ADMIN:
		{
			htmlButton = ClientUtils.makeEditButton();
			if (currentUser != null)
			{
				// find capabilities
			}
			break;
		}
		
		case ObidosConstants.CELL_TYPE_EDIT_GLOBAL_TEMPLATE:
		{
			boolean createdDuringInitialization = true;
			String tooltip = lang.noEditToolTip();
			if (userDefinedTypeDTO != null)
			{
				createdDuringInitialization = userDefinedTypeDTO.isSpenegoDeliveredElement();
			}
			if (this.currentUser != null)
			{
				boolean canEdit = this.currentUser.getUserDTO().getCapabilities().getCreateGlobalTemplate();
				if (canEdit && createdDuringInitialization)
				{
					tooltip = lang.spenegoSuppliednoEditToolTip();
					canEdit = false;
				}
				if (canEdit)
				{
					htmlButton = ClientUtils.makeEditButton();
				}
				else
				{
					htmlButton = ClientUtils.makeDisabledTextButton(lang.editButtonTitle(), tooltip);
				}
			}
			break;
		}

		// item shared with me. show Edit button if the owner gave permission
		// to allow editing
		case ObidosConstants.CELL_TYPE_EDIT_SHARED:
		{
			htmlButton = ClientUtils.makeEditButton();
			gwtLog("MMM in CELL_TYPE_EDIT_SHARED");
			if (sharedItemDTO != null)
			{
				PermissionDTO pdto = sharedItemDTO.getPermissions();
				gwtLog("MMMM container: " + sharedItemDTO.getContainerName() + " Owner of Item: " + sharedItemDTO.getOwnerFullname());
				gwtLog(" MMM can add? " + pdto.getMayAdd());
				gwtLog(" MMM can udpate? " + pdto.getMayUpdate());
				gwtLog(" MMM can own? " + pdto.getHasOwnershipControl());
				// Bug #131, Check for Edit permission
				// old code below
//				gwtLog(">>> " + sharedItemDTO.getName() + " has ownership control: " + pdto.getHasOwnershipControl());
				boolean rc = ClientUtils.fromBoolean(pdto.getMayUpdate());
				if (rc == false)
				{
					htmlButton = ClientUtils.makeDisabledTextButton(lang.editButtonTitle(), lang.noEditToolTip());

				}
				else
				{
					gwtLog("MMM Can Edit...");
					htmlButton = ClientUtils.makeEditButton();
				} 
			}
			break;
		}
		case ObidosConstants.CELL_TYPE_LIST:
		{
			htmlButton = ClientUtils.makeListButton();
			break;
		}
		case ObidosConstants.CELL_TYPE_RELINQUISH:
		{
			htmlButton = ClientUtils.makeRelinquishButton();
			break;
		}
		case ObidosConstants.CELL_TYPE_VIEW:
		{
			htmlButton = ClientUtils.makeViewButton();
			break;
		}

		case ObidosConstants.CELL_TYPE_COPY:
		{
			htmlButton = ClientUtils.makeCopyButton();
			break;
		}
		case ObidosConstants.CELL_TYPE_VIEW_NOTIFICATION_SHARED:
		{
			htmlButton = ClientUtils.makeNotificationButton(valueFunction.apply(notificationDTO), linkFunction.apply(notificationDTO));
			break;
		}
		case ObidosConstants.CELL_TYPE_AUDIT_LOG:
		{
			if (auditDTO != null)
			{
				gwtLog("Create regular text cell with tooltip for Audit message");
				htmlButton = ClientUtils.makeRegularText(auditDTO.getMessage(), auditDTO.getMessage());
			}
			break;
		}

		case ObidosConstants.CELL_TYPE_PHONE_NUMBER:
		{
			if (limitedUserDTO != null)
			{
				String phoneNumber = limitedUserDTO.getPhone();
				if (phoneNumber == null)
				{
					phoneNumber = lang.na();
				}
				htmlButton = ClientUtils.makeRegularText(phoneNumber, phoneNumber);
			}
			break;
		}
	

		default:
		{
			htmlButton = ClientUtils.makeDisabledText("Unknown", "Could not determine cell type");
			break;
		}
		}
		sb.appendHtmlConstant(htmlButton);
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event,
			ValueUpdater<String> valueUpdater)
	{
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
		if ("click".equals(event.getType()))
		{
			EventTarget eventTarget = event.getEventTarget();
			if (parent.getFirstChildElement().isOrHasChild(Element.as(eventTarget)))
			{
				Object contextKey = context.getKey();
				String cn = contextKey.getClass().getSimpleName();
				Boolean shareable = false;
				sharedContainerDTO = null;
				limitedItemDTO = null;

				// we do not set value updater for some buttons, if we do
				// exception will be thrown
				if (cellType == ObidosConstants.CELL_TYPE_CONTAINER_SHAREABLE_NO_TOOLTIP
						|| cellType == ObidosConstants.CELL_TYPE_CONTAINER_SHARED_NO_TOOLTIP
						|| cellType == ObidosConstants.CELL_TYPE_ITEM_SHAREABLE_OR_PRIVATE // Community Bug# 24
						|| cellType == ObidosConstants.CELL_TYPE_GENERAL
						|| cellType == ObidosConstants.CELL_TYPE_LOCKED_USER
						|| cellType == ObidosConstants.CELL_TYPE_MARKED_DELETED_USER
						|| cellType == ObidosConstants.CELL_TYPE_ITEM_USER_GROUP
						|| cellType == ObidosConstants.CELL_TYPE_CONTAINER_USER_GROUP
						)
				{
					return;
				}

				if (cellType == ObidosConstants.CELL_TYPE_SHARE)
				{
					gwtLog("XX at onBrowserEvent");
					if (cn.equals(SharedContainerDTO.class.getSimpleName()))
					{
						gwtLog("XX ShareContainerDTO");
						gwtLog("revoke passed instace of ContainerDTO");
						sharedContainerDTO = (SharedContainerDTO) contextKey;
					}
					if (cn.equals(LimitedItemDTO.class.getSimpleName()))
					{
						gwtLog("XX Limited Item DTO");
						gwtLog("revoke passed instace of LimitedItemDTO");
						limitedItemDTO = (LimitedItemDTO) contextKey;
					}
					
					if (cn.equals(ContainerDTO.class.getSimpleName()))
					{
						gwtLog("XX ContainerDTO ..");
						containerDTO = (ContainerDTO) contextKey;
					}


					if (containerDTO != null)
					{
						Boolean isPrivate = containerDTO.getIsPrivate();
						// I noticed this thing can be null
						if (isPrivate == null)
						{
							isPrivate = true;
							shareable = false;
						}
						if (isPrivate)
						{
							shareable = false;
						} else
						{
							shareable = true;
						}
						if (shareable)
						{
							gwtLog("XXX XXX XXX");
							// if license does not allow don't set value updated
							if (! doesLicenseAllowContainerSharing())
							{
								return;
							}

							gwtLog("Set value updater: " + value);
							valueUpdater.update(value);
							return;
						} else
						{
							gwtLog("Don't set value updater: " + value);
							return;
						}
					}

					if (sharedContainerDTO == null && limitedItemDTO == null)
					{
						gwtLog("XX Both dtos are null, set value updater");
						valueUpdater.update(value);
						return;
					}

					if (limitedItemDTO != null)
					{
						shareable = limitedItemDTO.getShareable();
						if (shareable == null)
						{
							shareable = false;
						}
						if (shareable)
						{
							gwtLog("Set value updater: " + value);
							valueUpdater.update(value);
							return;
						} else
						{
							return;
						}
					}
				}
				else if (cellType == ObidosConstants.CELL_TYPE_CONTAINER_SHARED_WITH_USERS_GROUPS)
				{
					if (cn.equals(ContainerDTO.class.getSimpleName()))
					{
						gwtLog("revoke passed instance of SharedContainerDTO");
						containerDTO = (ContainerDTO) contextKey;
						if (containerDTO != null)
						{
							boolean shared = ClientUtils.isContainerSharedWithSomeone(containerDTO);
							if (shared)
							{
								valueUpdater.update(value);
								return;
							} else
							{
								return;
							}
						}
					}
				}
				else if (cellType == ObidosConstants.CELL_TYPE_EDIT_SHARED)
				{
					if (cn.equals(SharedItemDTO.class.getSimpleName()))
					{
						SharedItemDTO sidto = (SharedItemDTO) contextKey;
						PermissionDTO pdto = sidto.getPermissions();
						boolean rc = ClientUtils.fromBoolean(pdto.getMayUpdate());
						gwtLog("rc: " + rc);
						if (rc)
						{
							valueUpdater.update(value);
							return;
						} else
						{
							return;
						}
					}
				}
				else if (cellType == ObidosConstants.CELL_TYPE_EDIT_GLOBAL_TEMPLATE)
				{
					if (this.currentUser != null)
					{
						if (this.currentUser.getUserDTO().getCapabilities().getCreateGlobalTemplate())
						{
							valueUpdater.update(value);
							return;
						}
						else
						{
							return;
						}
					}
				}
				else if (cellType == ObidosConstants.CELL_TYPE_AUDIT_LOG)
				{
					return;
				}
				else if (cellType == ObidosConstants.CELL_TYPE_PHONE_NUMBER)
				{
					return;
				}
				else if (cellType == ObidosConstants.CELL_TYPE_VIEW_NOTIFICATION_SHARED)
				{
					if (cn.equals(NotificationDTO.class.getSimpleName()))
					{
						notificationDTO = (NotificationDTO) contextKey;
						if (notificationDTO != null)
						{
							valueUpdater.update(valueFunction.apply(notificationDTO));
						}
					}
				} else if (cellType == ObidosConstants.CELL_TYPE_REVOKE)
				{
					if (cn.equals(SharedContainerDTO.class.getSimpleName()))
					{
						gwtLog("revoke passed instace of SharedContainerDTO");
						sharedContainerDTO = (SharedContainerDTO) contextKey;
					}
					if (cn.equals(LimitedItemDTO.class.getSimpleName()))
					{
						gwtLog("revoke passed instace of LimitedItemDTO");
						limitedItemDTO = (LimitedItemDTO) contextKey;
					}
					if (sharedContainerDTO != null)
					{
						if (sharedContainerDTO.getShared() || sharedContainerDTO.getGroupShared())
						{
							valueUpdater.update(value);
							return;
						} else
						{
							gwtLog("Don't set value fupdate container");
							return;
						}
					}
					// the above code about ShareContainerDTO may be redundant
					if (cn.equals(ContainerDTO.class.getSimpleName()))
					{
						containerDTO = (ContainerDTO) contextKey;
						if (containerDTO.getShared() || containerDTO.getGroupShared())
						{
							valueUpdater.update(value);
							return;
						}
						else
						{
							return;
						}
					}
					if (limitedItemDTO != null)
					{
						Boolean shared = limitedItemDTO.getShared();
						if (shared == null) // just in case
						{
							shared = true;
						}
						if (shared)
						{
							valueUpdater.update(value);
							return;
						} else
						{
							return;

						}
					}
				} else if (cellType == ObidosConstants.CELL_TYPE_SHARED_WITH_OTHERS)
				{
					if (cn.equals(ContainerDTO.class.getSimpleName()))
					{
						containerDTO = (ContainerDTO) contextKey;
					}
					if (cn.equals(LimitedItemDTO.class.getSimpleName()))
					{
						limitedItemDTO = (LimitedItemDTO) contextKey;
					}

					if (containerDTO != null)
					{
						Boolean shared = containerDTO.getShared();

						boolean isShared = false;
						if (shared != null)
						{
							isShared = shared.booleanValue();
						}
						if (isShared)
						{
							valueUpdater.update(value);
							return;
						}
					}
					if (limitedItemDTO != null)
					{
						Boolean shared = limitedItemDTO.getShared();
						boolean isSharedWithSomeone = false;
						if (shared != null)
						{
							isSharedWithSomeone = shared.booleanValue();
						}
						if (isSharedWithSomeone)
						{
							valueUpdater.update(value);
							return;
						}
					}
				} else if (cellType == ObidosConstants.CELL_TYPE_TAKE_CONTAINER_OWNERSHIP)
				{
					if (cn.equals(SharedContainerDTO.class.getSimpleName()))
					{
						sharedContainerDTO = (SharedContainerDTO) contextKey;
						if (sharedContainerDTO != null)
						{
							boolean canOwn = ClientUtils.fromBoolean(sharedContainerDTO.getOwnershipControl());
							if (canOwn)
							{
								valueUpdater.update(value);
								return;
							}
						}
					}
				} else if (cellType == ObidosConstants.CELL_TYPE_ADD)
				{
					if (cn.equals(SharedContainerDTO.class.getSimpleName()))
					{
						sharedContainerDTO = (SharedContainerDTO) contextKey;
						// Bug #131 change Update to Add
						boolean mayAdd = ClientUtils.fromBoolean(sharedContainerDTO.getAddPermitted());
						if (mayAdd)
						{
							valueUpdater.update(value);
							return;
						}
					}
					else
					{
						valueUpdater.update(value);
					}
					
				} else
				{
					gwtLog("UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU");
					valueUpdater.update(value);
				}
			}
		}
	}
	
	private boolean doesLicenseAllowContainerSharing()
	{
		if (containerDTO != null)
		{
			CurrentUser cuser = this.currentUser;
			if (cuser != null)
			{
				UserDTO userDTO = cuser.getUserDTO();
				if (userDTO != null)
				{
					LicenseStats lstats = userDTO.getLicense();
					if (lstats != null)
					{
						return ClientUtils.fromBoolean(lstats.getSupportsContainerSharing());
					}
				}
			}
		}
		return false;
	}

	private void gwtLog(String message)
	{
		ClientUtils.gwtLog(this.getClass().getSimpleName(), message);
	}

}
