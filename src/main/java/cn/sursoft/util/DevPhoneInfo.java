package cn.sursoft.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cn.sursoft.Wire;

/**
 * Created by gtguo on 5/26/2017.
 */

class DevPhoneInfo {
    private ArrayList<String> properties = new ArrayList<>();
    private HashMap<String,String> devPropertiesHashMap = new HashMap<>();

    public DevPhoneInfo(){
        properties.add("imei");
        properties.add("imsi");
        properties.add("phoneNumber");
        properties.add("iccid");
        properties.add("operator");
        properties.add("network");
        properties.add("rom");
    }

    public ArrayList<String> getDevProperties(){
        return this.properties;
    }
    public HashMap<String,String> getDevPropertiesHashMap(){
        return this.devPropertiesHashMap;
    }
    /***********
     public void setDevPropertiesHashMap(HashMap<String,String> hs){
     this.devPropertiesHashMap = hs;
     }
     *********/
    public Wire.Envelope getPropertiesEnvelope(){
        Wire.GetPropertiesRequest.Builder getPropertiesBuilder= Wire.GetPropertiesRequest.newBuilder();
        Iterator<String> iterator = getDevProperties().iterator();
        while(iterator.hasNext()){
            getPropertiesBuilder.addProperties(iterator.next());
        }
        Wire.GetPropertiesRequest request = getPropertiesBuilder.build();

        Wire.Envelope.Builder envelopBuilder1 = Wire.Envelope.newBuilder();
        envelopBuilder1.setType(Wire.MessageType.GET_PROPERTIES);
        envelopBuilder1.setMessage(request.toByteString());
        Wire.Envelope envelope1 = envelopBuilder1.build();
        return envelope1;
    }
}
