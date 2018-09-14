package im.adamant.android.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.adamant.android.ui.entities.Chat;
import im.adamant.android.ui.entities.Contact;
import im.adamant.android.ui.messages_support.entities.AbstractMessage;

public class ChatsStorage {
    //TODO: So far, the manipulation of the chat lists is entrusted to this interactor, but perhaps over time it's worth changing
    //TODO: Multithreaded access to properties can cause problems in the future
    private HashMap<String, List<AbstractMessage>> messagesByChats = new HashMap<>();
    private List<Chat> chats = new ArrayList<>();
    private long contactsVersion = 0;

    public List<Chat> getChatList() {
        return chats;
    }

    public List<AbstractMessage> getMessagesByCompanionId(String companionId) {
        List<AbstractMessage> requestedMessages = messagesByChats.get(companionId);

        if (requestedMessages == null){return new ArrayList<>();}

        return requestedMessages;
    }

    public void addNewChat(Chat chat) {
        int index = chats.indexOf(chat);
        if (index == -1){
            chats.add(chat);
            messagesByChats.put(chat.getCompanionId(), new ArrayList<>());
        } else {
            //TODO: This code is needed to get the public key of the sender. This is wrong.
            //TODO: If the sender did not respond, his avtar will not be displayed
            Chat storedChat = chats.get(index);
            String companionPublicKey = storedChat.getCompanionPublicKey();

            if ((companionPublicKey == null) && (chat.getCompanionPublicKey() != null)){
                storedChat.setCompanionPublicKey(chat.getCompanionPublicKey());
            }
        }
    }


    public void addMessageToChat(AbstractMessage message) {
        List<AbstractMessage> messages = messagesByChats.get(message.getCompanionId());

        if (messages != null) {
            //If we sent this message and it's already in the list
            if (!messages.contains(message)){
                messages.add(message);
            }
        } else {
            List<AbstractMessage> newMessageBlock = new ArrayList<>();
            newMessageBlock.add(message);
            messagesByChats.put(message.getCompanionId(), newMessageBlock);
        }
    }

    public void updateLastMessages() {
        //Setting last message to chats
        for(Chat chat : chats){
            List<AbstractMessage> messages = messagesByChats.get(chat.getCompanionId());
            if (messages != null && messages.size() > 0){
                AbstractMessage mes = messages.get(messages.size() - 1);
                if (mes != null){chat.setLastMessage(mes);}
            }
        }
    }

    public void refreshContacts(Map<String, Contact> contacts, long currentVersion) {
        if (currentVersion > contactsVersion){
            for (Map.Entry<String, Contact> contactEntry : contacts.entrySet()){
                String companionId = contactEntry.getKey();
                Contact contact = contactEntry.getValue();

                Chat chat = new Chat();
                chat.setCompanionId(companionId);

                if (chats.contains(chat)) {
                    int index = chats.indexOf(chat);
                    Chat originalChat = chats.get(index);
                    originalChat.setTitle(contact.getDisplayName());
                }
            }
        }
    }

    public Chat findChatByCompanionId(String companionId) {
        Chat chat = new Chat();
        chat.setCompanionId(companionId);

        if (chats.contains(chat)){
            int index = chats.indexOf(chat);
            return chats.get(index);
        } else {
            return null;
        }
    }

    public Map<String, Contact> getContacts() {
        Map<String, Contact> contacts = new HashMap<>();

        for (Chat chat : chats) {
            if (!chat.getTitle().equalsIgnoreCase(chat.getCompanionId())){
                Contact contact = new Contact();
                contact.setDisplayName(chat.getTitle());
                contacts.put(chat.getCompanionId(), contact);
            }
        }

        return contacts;
    }

    public void cleanUp() {
        chats.clear();
        messagesByChats.clear();
        contactsVersion = 0;
    }
}
