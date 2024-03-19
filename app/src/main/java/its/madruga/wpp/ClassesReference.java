package its.madruga.wpp;

public class ClassesReference {

    public static class ChangeColors {

        public static String customDrawable1 = "X.062"; // expandableWidgetHelper

        public static String customDrawable2 = "X.05n"; // Compatibility shadow requested but can\'t be drawn for all operations in this shape.
        public static String customDrawable3 = "X.0Na";
    }
    public static class AutoReboot {
        public static String autoreboot = "X.19r"; // app-init/application foregrounded
    }

    public static class AntiRevoke {
        public static String classMessage = "X.1jx";
        public static String threadMessage = "X.1mr";
        public static String onResume = "Bhx"; // com.whatsapp.Conversation -> onResume
        public static String onStart = "Bhy"; // com.whatsapp.Conversation -> onStart
        public static String convChatField = "A03";  // You can't send messages in this chat because Business Search isn't launched in your country
        public static String chatJidField = "A46"; // 6 acima do Lcom/whatsapp/mentions/MentionableEntry;
        public static String classRevokeMessage = "X.1SZ"; // msgstore/edit/revoke
        public static String methodRevokeMessage = "A04"; // msgstore/edit/revoke
        public static String fieldMessageKey = "A1L";
        public static String bubbleViewClass = "X.2RD";
        public static String bubbleViewMethod = "A1l"; // method com ID da string "Pinned" da classe acima
        public static String unknownStatusPlaybackMethod = "A0A";
        public static int iconId = 0x7f08099e;
        public static int stringId = 0x7f1212ff;
    }

    public static class GhostMode {
        public static String methodName = "A01";
        public static String param1 = "X.1ZQ";
        public static String param2 = "X.12O";
    }

    public static class HideForward {
        public static String classMessageInfo = AntiRevoke.classMessage;
        public static String methodSetForward = "A0a";
        public static String rightCallerClass = "X.1Z3";
    }

    public static class HideView {
        public static String classMessage = AntiRevoke.classMessage;
        public static String classMessageReceipt = "X.1E6";
        public static String methodMessageReceipt = "A09";
        public static String methodHideView = "A03";
    }

    public static class Others {
        public static String classObsoleto = "X.10K";
        public static String methodObsoleto = "A01"; // number format not valid
        public static String classProps = "X.0zQ";
        public static String methodProps = "A01"; // Unknown boolean
        public static String paramProps = "X.0zk";
        public static String paramProps2 = classProps;
    }

    public static class TimeModification {
        public static String classFormat = "X.3em"; // aBhHKm
        public static String paramFormat = "X.0vG"; // whatsapplocale/saveandapplylanguage/language to save:
        public static String methodFormat = "A01"; // A01(LX/0vG;J)Ljava/lang/String;
    }

    public static class ViewOnce {
        public static String[] vClasses = {
                "X.1kf", "X.1m4",
                "X.1mC", "X.1nD"
        };
        public static String methodName = "BsQ";
        public static String sendReadClass = "X.1qj";
        public static String menuMethod = "A1V";
        public static String menuIntField = "A1y";
        public static String initIntField = "A03";
        public static String slaMethod = "A07";
        public static int downloadDrawable = 0x7f080197;
        public static String methodMenu = "A1V"; // MediaViewFragment > Menu; (com MenuInflater)
    }

    public static class StatusDownload {
        public static String classMedia = "X.1l5";
        public static String setPageActiveMethod = "A0E"; // playbackFragment/setPageActive no-messages
        public static String fieldList = "A0n";
        public static String classMenuStatus = "X.3jS"; // chatSettingsStore
        public static String fieldFile = "A0I";
        public static String aClass = "X.0XU";
        public static int downloadStringId = 0x7f120485;
        public static int findItem = 0x7f0b1009;
        public static int addItem = findItem;
    }

    public static class FreezeLastSeen {
        public static String classSendAvailable = "X.1aR";
        public static String methodSendAvailable = "A02"; // presencestatemanager/setAvailable/new-state:
        public static String param1 = "X.1aR";
    }

    public static class LimitShare {
        // ContactPickerFragment > View;LX/
        public static String methodShareLimit = "A1r"; // (plurals): Broadcast to a maximum of
        public static String param1 = "X.155";
        public static String booleanField = "A36";
    }

    public static class ShowBioAndName {
        public static String setTitleMethod = "A0P";
        public static String setSummaryMethod = "A0O";
    }

    public static class HideReceipt {
        public static String mainClass = "X.6gL"; // privacy_token com false embaixo, provavelmente o 3 resultado
        public static String param1Class = GhostMode.param2;
        public static String param4Class = "X.1k2";
    }

    public static class MediaQuality {
        public static String vClassQuality = "X.1Hs"; // videopreview/bad video
        public static String vMethodResolution = "A05"; // (III)
        public static String vParam1 = "X.6LB"; // 2 LX's retornando Pair
        public static String vParam2 = "X.6HX"; // 2 LX's retornando Pair
        public static String vmethod = "A06"; // o metodo encontrado acima
        public static String vmethod2 = "A01"; // "1080"
        public static String imainClass = Others.classProps;
        public static String imethod = "A00";
        public static String iparam1 = Others.paramProps;

    }

    // Updated
    public static class BubbleColors {
        public static String bubblesClass = "X.3oR"; // balloon_outgoing_normal
        public static String balloonIncomingNormal = "BBr";
        public static String balloonIncomingNormalExt = "BBs";
        public static String balloonOutgoingNormal = "BDw";
        public static String balloonOutgoingNormalExt = "BDx";
    }

    // Updated
    public static class Databases {
        public static String msgstoreClass = "X.13k";
        public static String axolotlClass = "X.1BF";
    }

    // Updated
    public static class DndMode {
        public static String mainClass = "X.0zI"; // MessageHandler/start
        public static String mainMethod = "A02";
        public static int iconOn = 0x7f0806ac;
        public static int iconOff = 0x7f0806ad;
    }

    // Updated
    public static class ChatsFilter {
        public static String classGetTab = "X.1SP";
        public static String methodGetTab = "A0M"; // Invalid tab 600:
        public static String classTabsList = "X.16H"; // homeFabManager
        public static String fieldTabsList = "A2Y"; // onCreate > 1st sget-object
        public static String classTabName = classGetTab;
        public static String methodTabName = "A0N"; // The item position should be less or equal to:
        public static String methodTabIcon = "A3l";
        public static String methodTabInstance = "A1e"; // Method without parameters returning List
        public static int nameId = 0x7f120528;
        public static int tabIconId = 0x7f0803df; // home_tab_communities_selector
    }
}
