package org.enocean.java.packets;

import java.nio.ByteBuffer;

import org.enocean.java.address.EnoceanId;

public class RadioPacket extends BasicPacket {

    public static final byte PACKET_TYPE = 0x01;

    public static final byte RADIO_TYPE_4BS = (byte) 0xA5;
    public static final byte RADIO_TYPE_VLD = (byte) 0xD2;

    private EnoceanId senderId;
    private int repeaterCount;
    private byte status;

    private byte subTelNum;
    private EnoceanId destinationId;
    private byte dBm;
    private byte securityLevel;

    public RadioPacket(RawPacket rawPacket) {
        super(rawPacket);
    }

    public RadioPacket() {
        header.setPacketType(PACKET_TYPE);
    }

    /**
     * @param subTelNum
     *            Number of subTelegram. Send = 3, receive = 1..x
     * @param destinationId
     *            Destination Id (4 byte). Broadcast Radio = FF FF FF FF, ADT
     *            radio: Destination ID (address)
     * @param dBm
     *            Send case: FF, Receive case: best RSSI value of all received
     *            subtelegrams (value decimal without minus)
     * @param securityLevel
     *            Security Level. 0 = unencrypted, x = type of encryption
     */
    public RadioPacket(byte[] data, byte subTelNum, int destinationId, byte dBm, byte securityLevel) {
        this(subTelNum, destinationId, dBm, securityLevel);
        payload.setData(data);
    }

    /**
     * @param subTelNum
     *            Number of subTelegram. Send = 3, receive = 1..x
     * @param destinationId
     *            Destination Id (4 byte). Broadcast Radio = FF FF FF FF, ADT
     *            radio: Destination ID (address)
     * @param dBm
     *            Send case: FF, Receive case: best RSSI value of all received
     *            subtelegrams (value decimal without minus)
     * @param securityLevel
     *            Security Level. 0 = unencrypted, x = type of encryption
     */
    protected RadioPacket(byte subTelNum, int destinationId, byte dBm, byte securityLevel) {
        this.subTelNum = subTelNum;
        this.destinationId = EnoceanId.fromInt(destinationId);
        this.dBm = dBm;
        this.securityLevel = securityLevel;
        header.setPacketType(PACKET_TYPE);
    }

    @Override
    protected void parseData() {
        byte[] data = payload.getData();
        int length = data.length;
        senderId = EnoceanId.fromByteArray(data, length - 5);
        status = data[length - 1];
        repeaterCount = (status & 0x0F);
    }

    public ParameterMap getAllParameterValues() {
        ParameterMap result = new ParameterMap();
        return result;
    }

    @Override
    protected void fillOptionalData() {
        ByteArrayWrapper wrapper = new ByteArrayWrapper();
        wrapper.addByte(subTelNum);
        wrapper.addBytes(destinationId.toBytes());
        wrapper.addByte(dBm);
        wrapper.addByte(securityLevel);
        payload.setOptionalData(wrapper.getArray());
    }

    @Override
    protected void parseOptionalData() {
        ByteBuffer optionalDataBytes = ByteBuffer.wrap(payload.getOptionalData());
        subTelNum = optionalDataBytes.get();
        destinationId = EnoceanId.fromInt(optionalDataBytes.getInt());
        dBm = optionalDataBytes.get();
        securityLevel = optionalDataBytes.get();
    }

    public EnoceanId getSenderId() {
        return senderId;
    }

    @Override
    public String toString() {
        return super.toString() + ", [sender=" + senderId + ", repeaterCount=" + repeaterCount + "]";
    }

}