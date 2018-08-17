package im.adamant.android.helpers;

import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

import im.adamant.android.rx.ObservableRxList;
import im.adamant.android.core.entities.ServerNode;
import io.reactivex.disposables.Disposable;

public class Settings {
    private static final String NODES_KEY = "nodes_key";
    private static final String ACCOUNT_KEY_PAIR = "account_key_pair";
    private static final String KEY_PAIR_MUST_BE_STORED = "key_pair_must_be_stored";

    private ObservableRxList<ServerNode> nodes = new ObservableRxList<>();
    private String accountKeypair = "";
    private boolean isKeyPairMustBeStored;

    private SharedPreferences preferences;

    public Settings(SharedPreferences preferences) {
        this.preferences = preferences;

        accountKeypair = this.preferences.getString(ACCOUNT_KEY_PAIR, "");
        isKeyPairMustBeStored = this.preferences.getBoolean(KEY_PAIR_MUST_BE_STORED, false);

        loadNodes();
    }

    public void addNode(ServerNode node) {
        nodes.add(node);
        updateNodes();
    }

    public void removeNode(ServerNode node) {
        if (nodes.contains(node)){
            nodes.remove(node);
            updateNodes();
        }
    }

    public ObservableRxList<ServerNode> getNodes() {
        return nodes;
    }

    public String getAccountKeypair() {
        return accountKeypair;
    }

    public void setAccountKeypair(String accountKeypair) {
        this.accountKeypair = accountKeypair;
        this.preferences
                .edit()
                .putString(ACCOUNT_KEY_PAIR, accountKeypair)
                .apply();
    }

    public boolean isKeyPairMustBeStored() {
        return isKeyPairMustBeStored;
    }

    public void setKeyPairMustBeStored(boolean keyPairMustBeStored) {
        isKeyPairMustBeStored = keyPairMustBeStored;
        this.preferences
                .edit()
                .putBoolean(KEY_PAIR_MUST_BE_STORED, keyPairMustBeStored)
                .apply();
    }

    private void updateNodes() {
        Disposable subscribe = nodes.getCurrentList()
                .map(ServerNode::getUrl)
                .toList()
                .subscribe((list) -> {
                    Set<String> set = new HashSet<>(list);
                    preferences
                            .edit()
                            .putStringSet(NODES_KEY, set)
                            .apply();
                });
    }

    private Set<String> getDefaultNodes() {
        Set<String> defaults = new HashSet<>();
        defaults.add("https://clown.adamant.im");
        defaults.add("https://lake.adamant.im");
        defaults.add("https://endless.adamant.im");

        return defaults;
    }

    private void loadNodes(){
        Set<String> nodeUrls = preferences.getStringSet(NODES_KEY, getDefaultNodes());

        if (nodeUrls.size() == 0){
            nodeUrls = getDefaultNodes();
            preferences
                    .edit()
                    .putStringSet(NODES_KEY, nodeUrls)
                    .apply();
        }

        for (String nodeUrl : nodeUrls) {
            nodes.add(new ServerNode(nodeUrl));
        }
    }
}