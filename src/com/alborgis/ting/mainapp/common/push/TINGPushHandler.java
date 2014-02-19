package com.alborgis.ting.mainapp.common.push;

import org.json.JSONException;
import org.json.JSONObject;

import com.alborgis.ting.base.log.Milog;
import com.alborgis.ting.mainapp.R;
import com.alborgis.ting.mainapp.games.geocache_battle.GeocacheBattleMapActivity;
import com.alborgis.ting.mainapp.games.multiplayer.JoinSlotActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class TINGPushHandler {
	
	public static final class EVENTS {
		public static final String USER_JOINED_TO_SLOT			=	"1";
		public static final String USER_START_SLOT_PLAY			=	"2";
		public static final String USER_LEAVED_SLOT				=	"3";
	}
	
	public static final class GEOCACHE_GAME_BATTLE_EVENTS {
		public static final String USER_FOUND_TREASURE			=	"100";
		public static final String USER_WASTE_ATTEMPTS			=	"101";
	}
	
	public static void handleNotificationIntent(Context _ctx, Intent _notifIntent){
		Bundle extras = _notifIntent.getExtras();
		Milog.d("Received: " + extras.toString());
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(_ctx);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(_notifIntent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {

            	// If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

            	String msg = extras.getString("message");
            	String data = extras.getString("data");
            	if(data != null){
            		try {
	            		JSONObject dataDic = new JSONObject(data);
	            		Milog.d("Object: " + dataDic.toString());
	            		
	                	String event = dataDic.getString("event");
	                	String title = dataDic.getString("title");
	                	
	                	// Comprobar por el evento que nos env’an y mostrar notificacion
	                	if(event.equals(EVENTS.USER_JOINED_TO_SLOT)){
	                		// Si el evento es que un usuario se ha unido a una partida...
	                		String slot_nid = dataDic.getString("slot_nid");
	                		Intent intent = new Intent(_ctx, JoinSlotActivity.class);
	                		intent.putExtra(JoinSlotActivity.PARAM_KEY_SLOT_NID, slot_nid);
	                		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                		int idNot = 1;
	                		try{
	                			idNot = Integer.parseInt(slot_nid);
	                		}catch(NumberFormatException ex){
	                			
	                		}
	                		// S—lo mostrar notificaci—n si la pantalla de unirse al juego y la del mapa est‡n invisibles
	                		if( !JoinSlotActivity.isVisible && !GeocacheBattleMapActivity.isVisible){
	                			showNotification(_ctx, idNot, R.drawable.ic_launcher, title, msg, true, false, intent);
	                		}
	                		
	                	}else if(event.equals(EVENTS.USER_START_SLOT_PLAY)){
	                		// Si el evento es que un usuario ha comenzado a jugar la partida...
	                		String slot_nid = dataDic.getString("slot_nid");
	                		Intent intent = new Intent(_ctx, JoinSlotActivity.class);
	                		intent.putExtra(JoinSlotActivity.PARAM_KEY_SLOT_NID, slot_nid);
	                		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                		int idNot = 1;
	                		try{
	                			idNot = Integer.parseInt(slot_nid);
	                		}catch(NumberFormatException ex){
	                			
	                		}
	                		// S—lo mostrar notificaci—n si la pantalla de unirse al juego y la del mapa est‡n invisibles
	                		if( !JoinSlotActivity.isVisible && !GeocacheBattleMapActivity.isVisible){
	                			showNotification(_ctx, idNot, R.drawable.ic_launcher, title, msg, true, false, intent);
	                		}
	                	}else if(event.equals(EVENTS.USER_LEAVED_SLOT)){
	                		// Si el evento es que un usuario ha comenzado a jugar la partida...
	                		String slot_nid = dataDic.getString("slot_nid");
	                		Intent intent = new Intent(_ctx, JoinSlotActivity.class);
	                		intent.putExtra(JoinSlotActivity.PARAM_KEY_SLOT_NID, slot_nid);
	                		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                		int idNot = 1;
	                		try{
	                			idNot = Integer.parseInt(slot_nid);
	                		}catch(NumberFormatException ex){
	                			
	                		}
	                		// S—lo mostrar notificaci—n si la pantalla de unirse al juego y la del mapa est‡n invisibles
	                		if( !JoinSlotActivity.isVisible && !GeocacheBattleMapActivity.isVisible){
	                			showNotification(_ctx, idNot, R.drawable.ic_launcher, title, msg, true, false, intent);
	                		}
	                	}else if(event.equals(GEOCACHE_GAME_BATTLE_EVENTS.USER_FOUND_TREASURE)){
	                		// Si el evento es que un usuario ha comenzado a jugar la partida...
	                		String slot_nid = dataDic.getString("slot_nid");
	                		Intent intent = new Intent(_ctx, JoinSlotActivity.class);
	                		intent.putExtra(JoinSlotActivity.PARAM_KEY_SLOT_NID, slot_nid);
	                		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                		int idNot = 1;
	                		try{
	                			idNot = Integer.parseInt(slot_nid);
	                		}catch(NumberFormatException ex){
	                			
	                		}
	                		// S—lo mostrar notificaci—n si la pantalla de unirse al juego y la del mapa est‡n invisibles
	                		if( !JoinSlotActivity.isVisible && !GeocacheBattleMapActivity.isVisible){
	                			showNotification(_ctx, idNot, R.drawable.ic_launcher, title, msg, true, false, intent);
	                		}
	                	}else if(event.equals(GEOCACHE_GAME_BATTLE_EVENTS.USER_WASTE_ATTEMPTS)){
	                		// Si el evento es que un usuario ha comenzado a jugar la partida...
	                		String slot_nid = dataDic.getString("slot_nid");
	                		Intent intent = new Intent(_ctx, JoinSlotActivity.class);
	                		intent.putExtra(JoinSlotActivity.PARAM_KEY_SLOT_NID, slot_nid);
	                		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                		int idNot = 1;
	                		try{
	                			idNot = Integer.parseInt(slot_nid);
	                		}catch(NumberFormatException ex){
	                			
	                		}
	                		// S—lo mostrar notificaci—n si la pantalla de unirse al juego y la del mapa est‡n invisibles
	                		if( !JoinSlotActivity.isVisible && !GeocacheBattleMapActivity.isVisible){
	                			showNotification(_ctx, idNot, R.drawable.ic_launcher, title, msg, true, false, intent);
	                		}
	                	}
	                	
	                	// Enviar un mensaje a todas las activities para comunicarlas
	                    Intent broadCast = new Intent("MyGCMMessageReceived");
	                    broadCast.putExtras(extras);
	                    _ctx.sendBroadcast(broadCast);

                	
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	
                	
            	}
            	
            	
            	
            	
        		
            }
        }
	}
	
	
	// Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private static void showNotification(Context _ctx, int notificationId, int resIdIcon, String _title, String _msg, boolean autoCancel, boolean onGoing, Intent intent) {
    	NotificationManager mNotificationManager = (NotificationManager) _ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        /*PendingIntent contentIntent = PendingIntent.getActivity(_ctx, 0,
                intent, 0);*/
    	PendingIntent contentIntent = PendingIntent.getActivity(_ctx, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =  new NotificationCompat.Builder(_ctx)
        .setSmallIcon(resIdIcon)
        .setContentTitle(_title)
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(_msg))
        .setAutoCancel(autoCancel)
        .setOngoing(onGoing)
        .setContentText(_msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(notificationId, mBuilder.build());
    }
    
    
    public static void cancelNotification(Context ctx, int notificationId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notificationId);
    }
    
    
    
}
