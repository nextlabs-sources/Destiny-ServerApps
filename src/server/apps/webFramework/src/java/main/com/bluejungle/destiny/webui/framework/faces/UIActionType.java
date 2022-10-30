/*
 * Created on May 3, 2005
 * 
 * All sources, binaries and HTML pages (C) copyright 2004 by Blue Jungle Inc.,
 * Redwood City CA, Ownership remains with Blue Jungle Inc, All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.webui.framework.faces;

import java.util.Set;

import com.bluejungle.destiny.appframework.CommonConstants;
import com.bluejungle.domain.action.ActionEnumType;
import com.bluejungle.framework.patterns.EnumBase;

/**
 * The UI action type enumeration allows going from an action type to an action
 * name that can be displayed and localized for the end user.
 * 
 * @author ihanen
 * @version $Id:
 *          //depot/main/Destiny/main/src/server/apps/webFramework/src/java/main/com/bluejungle/destiny/webui/framework/faces/UIActionType.java#1 $
 */

public class UIActionType extends UIEnumBase {

    public static final UIActionType ACTION_UI_CHANGE_ATTRIBUTE = new UIActionType(ActionEnumType.ACTION_CHANGE_ATTRIBUTES.getName(), "action_change_attribute", 0);
    public static final UIActionType ACTION_UI_CHANGE_SECURITY_BUNDLE_KEY = new UIActionType(ActionEnumType.ACTION_CHANGE_SECURITY.getName(), "action_change_security", 1);
    public static final UIActionType ACTION_UI_COPY_BUNDLE_KEY = new UIActionType(ActionEnumType.ACTION_COPY.getName(), "action_copy", 2);
    public static final UIActionType ACTION_UI_CREATE_BUNDLE_KEY = new UIActionType(ActionEnumType.ACTION_EDIT.getName(), "action_create", 3);
    public static final UIActionType ACTION_UI_CUT_PASTE_BUNDLE_KEY = new UIActionType(ActionEnumType.ACTION_PASTE.getName(), "action_cut_paste", 4);
    public static final UIActionType ACTION_UI_DELETE_BUNDLE_KEY = new UIActionType(ActionEnumType.ACTION_DELETE.getName(), "action_delete", 5);
    public static final UIActionType ACTION_UI_EMBED_BUNDLE_KEY = new UIActionType(ActionEnumType.ACTION_EMBED.getName(), "action_embed", 6);
    public static final UIActionType ACTION_UI_MOVE_BUNDLE_KEY = new UIActionType(ActionEnumType.ACTION_MOVE.getName(), "action_move", 7);
    public static final UIActionType ACTION_UI_OPEN_BUNDLE_KEY = new UIActionType(ActionEnumType.ACTION_OPEN.getName(), "action_open", 9);
    public static final UIActionType ACTION_UI_PRINT_BUNDLE_KEY = new UIActionType(ActionEnumType.ACTION_PRINT.getName(), "action_print", 8);
    public static final UIActionType ACTION_UI_RENAME_BUNDLE_KEY = new UIActionType(ActionEnumType.ACTION_RENAME.getName(), "action_rename", 10);
    public static final UIActionType ACTION_UI_SEND_EMAIL_BUNDLE_KEY = new UIActionType(ActionEnumType.ACTION_SEND_EMAIL.getName(), "action_send_email", 11);
    public static final UIActionType ACTION_UI_SEND_IM_BUNDLE_KEY = new UIActionType(ActionEnumType.ACTION_SEND_IM.getName(), "action_send_im", 12);
    public static final UIActionType ACTION_UI_ABNORMAL_AGENT_SHUTDOWN = new UIActionType(ActionEnumType.ACTION_ABNORMAL_AGENT_SHUTDOWN.getName(), "action_agent_abnormal_shutdown", 13);
    public static final UIActionType ACTION_UI_AGENT_USER_LOGIN = new UIActionType(ActionEnumType.ACTION_AGENT_USER_LOGIN.getName(), "action_agent_user_login", 14);
    public static final UIActionType ACTION_UI_AGENT_USER_LOGOUT = new UIActionType(ActionEnumType.ACTION_AGENT_USER_LOGOUT.getName(), "action_agent_user_logout", 15);
    public static final UIActionType ACTION_UI_AGENT_READ_CONFIG = new UIActionType(ActionEnumType.ACTION_ACCESS_AGENT_CONFIG.getName(), "action_agent_access_config", 16);
    public static final UIActionType ACTION_UI_AGENT_READ_LOG = new UIActionType(ActionEnumType.ACTION_ACCESS_AGENT_LOGS.getName(), "action_agent_access_logs", 17);
    public static final UIActionType ACTION_UI_AGENT_READ_BINARIES = new UIActionType(ActionEnumType.ACTION_ACCESS_AGENT_BINARIES.getName(), "action_agent_access_bin", 18);
    public static final UIActionType ACTION_UI_AGENT_START = new UIActionType(ActionEnumType.ACTION_START_AGENT.getName(), "action_agent_start", 19);
    public static final UIActionType ACTION_UI_AGENT_STOP = new UIActionType(ActionEnumType.ACTION_STOP_AGENT.getName(), "action_agent_stop", 20);
    public static final UIActionType ACTION_UI_INVALID_BUNDLE = new UIActionType(ActionEnumType.ACTION_INVALID_BUNDLE.getName(), "action_invalid_bundle", 21);
    public static final UIActionType ACTION_UI_BUNDLE_RECEIVED = new UIActionType(ActionEnumType.ACTION_BUNDLE_RECEIVED.getName(), "action_bundle_received", 22);
    public static final UIActionType ACTION_UI_AGENT_READ_BUNDLE = new UIActionType(ActionEnumType.ACTION_ACCESS_AGENT_BUNDLE.getName(), "action_agent_access_bundle", 23);
    public static final UIActionType ACTION_UI_EXPORT = new UIActionType(ActionEnumType.ACTION_EXPORT.getName(), "action_export", 24);
    public static final UIActionType ACTION_UI_ATTACH = new UIActionType(ActionEnumType.ACTION_ATTACH.getName(), "action_attach", 25);
    public static final UIActionType ACTION_UI_RUN = new UIActionType(ActionEnumType.ACTION_RUN.getName(), "action_run", 26);
    public static final UIActionType ACTION_UI_AVD = new UIActionType(ActionEnumType.ACTION_AVD.getName(), "action_avd", 27);
    public static final UIActionType ACTION_UI_MEETING = new UIActionType(ActionEnumType.ACTION_MEETING.getName(), "action_meeting", 28);
    public static final UIActionType ACTION_UI_PRESENCE = new UIActionType(ActionEnumType.ACTION_PRESENCE.getName(), "action_presence", 29);
    public static final UIActionType ACTION_UI_SHARE = new UIActionType(ActionEnumType.ACTION_SHARE.getName(), "action_share", 30);
    public static final UIActionType ACTION_UI_RECORD = new UIActionType(ActionEnumType.ACTION_RECORD.getName(), "action_record", 31);
    public static final UIActionType ACTION_UI_QUESTION = new UIActionType(ActionEnumType.ACTION_QUESTION.getName(), "action_question", 32);
    public static final UIActionType ACTION_UI_VOICE = new UIActionType(ActionEnumType.ACTION_VOICE.getName(), "action_voice", 33);
    public static final UIActionType ACTION_UI_VIDEO = new UIActionType(ActionEnumType.ACTION_VIDEO.getName(), "action_video", 34);
    public static final UIActionType ACTION_UI_JOIN = new UIActionType(ActionEnumType.ACTION_JOIN.getName(), "action_join", 35);

    /**
     * Constructor
     * 
     * @param enunName
     *            enumeration name
     * @param bundleKeyName
     *            key in the resource bundle used for display value
     * @param type
     *            enumeration type
     */
    protected UIActionType(String enunName, String bundleKeyName, int type) {
        super(enunName, bundleKeyName, type);
    }

    /**
     * Returns the localized display value
     * 
     * @param enumeration
     *            enum to localize
     * @return the localized display value
     */
    public static String getDisplayValue(UIEnumBase enumeration) {
        return getDisplayValue(enumeration, CommonConstants.COMMON_BUNDLE_NAME);
    }

    /**
     * Retrieve an UIActionType instance by name
     * 
     * @param name
     *            the name of the UIActionType
     * @return the ActionEnumType associated with the provided name
     * @throws IllegalArgumentException
     *             if no UIActionType exists with the specified name
     */
    public static UIActionType getUIActionType(String name) {
        if (name == null) {
            throw new NullPointerException("name cannot be null.");
        }

        return getElement(name, UIActionType.class);
    }

    /**
     * Retrieves the set of all UIActionType enums
     * 
     * @return set of enums
     */
    public static Set<UIActionType> elements() {
        return EnumBase.elements(UIActionType.class);
    }
}
