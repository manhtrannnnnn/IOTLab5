package com.example.demoiot;

import static android.os.Build.VERSION_CODES.R;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {
    MQTTHelper mqttHelper;
    TextView txtTemp, txtHumi;
    LabeledSwitch btnLed, btnPump;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtTemp = findViewById(R.id.txtTemperature);
        txtHumi = findViewById(R.id.txtHumidity);
//        btnLed = findViewById(R.id.btnLed);
//        btnPump = findViewById(R.id.btnPUMP);
//        btnLed.setOnToggledListener(new OnToggledListener() {
//            @Override
//            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
//                if(isOn == true){
//                    sendDataMQTT("manhtrannnnnn/feeds/led", "1");
//                }else{
//                    sendDataMQTT("manhtrannnnnn/feeds/led", "0");
//                }
//            }
//        });
//        btnPump.setOnToggledListener(new OnToggledListener() {
//            @Override
//            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
//                if(isOn == true){
//                    sendDataMQTT("manhtrannnnnn/feeds/waterpump", "1");
//                }else{
//                    sendDataMQTT("manhtrannnnnn/feeds/waterpump", "0");
//                }
//            }
//        });
        startMQTT();
    }

    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            b = value.getBytes(Charset.forName("UTF-8"));
        }
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (MqttException e){
        }
    }

    public void startMQTT(){
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST", topic + "****" + message.toString() );
                if(topic.contains("temperature")){
                    txtTemp.setText(message.toString() + "°C");
                }else if(topic.contains("airhumidity")){
                    txtHumi.setText(message.toString() + "%");
                }else if(topic.contains("led")){
                    if(message.toString().equals("1")) {
                        btnLed.setOn(true);
                    }else{
                        btnLed.setOn(false);
                    }
                }else if(topic.contains("waterpump")){
                    if(message.toString().equals("1")) {
                        btnPump.setOn(true);
                    }else{
                        btnPump.setOn(false);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}