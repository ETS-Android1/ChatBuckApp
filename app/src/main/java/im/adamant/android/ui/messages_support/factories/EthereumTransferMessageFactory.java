package im.adamant.android.ui.messages_support.factories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.adamant.android.R;
import im.adamant.android.helpers.AdamantAddressProcessor;
import im.adamant.android.ui.entities.messages.EthereumTransferMessage;
import im.adamant.android.ui.holders.messages.AbstractMessageViewHolder;
import im.adamant.android.ui.holders.messages.EthereumTransferMessageViewHolder;
import im.adamant.android.ui.messages_support.builders.EthereumTransferMessageBuilder;
import im.adamant.android.ui.messages_support.builders.MessageBuilder;

public class EthereumTransferMessageFactory implements MessageFactory<EthereumTransferMessage> {
    private AdamantAddressProcessor adamantAddressProcessor;

    public EthereumTransferMessageFactory(AdamantAddressProcessor adamantAddressProcessor) {
        this.adamantAddressProcessor = adamantAddressProcessor;
    }

    @Override
    public MessageBuilder<EthereumTransferMessage> getMessageBuilder() {
        return new EthereumTransferMessageBuilder();
    }

    @Override
    public AbstractMessageViewHolder getViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item_etherium_transfer_message, parent, false);
        return new EthereumTransferMessageViewHolder(parent.getContext(), v, adamantAddressProcessor);
    }
}
