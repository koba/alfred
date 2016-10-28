package com.applink.ford.hellosdlandroid;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.exception.SdlExceptionCause;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.SdlProxyALM;
import com.smartdevicelink.proxy.callbacks.OnServiceEnded;
import com.smartdevicelink.proxy.callbacks.OnServiceNACKed;
import com.smartdevicelink.proxy.interfaces.IProxyListenerALM;
import com.smartdevicelink.proxy.rpc.AddCommand;
import com.smartdevicelink.proxy.rpc.AddCommandResponse;
import com.smartdevicelink.proxy.rpc.AddSubMenu;
import com.smartdevicelink.proxy.rpc.AddSubMenuResponse;
import com.smartdevicelink.proxy.rpc.AlertManeuverResponse;
import com.smartdevicelink.proxy.rpc.AlertResponse;
import com.smartdevicelink.proxy.rpc.ChangeRegistrationResponse;
import com.smartdevicelink.proxy.rpc.Choice;
import com.smartdevicelink.proxy.rpc.CreateInteractionChoiceSet;
import com.smartdevicelink.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteCommandResponse;
import com.smartdevicelink.proxy.rpc.DeleteFileResponse;
import com.smartdevicelink.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteSubMenuResponse;
import com.smartdevicelink.proxy.rpc.DiagnosticMessageResponse;
import com.smartdevicelink.proxy.rpc.DialNumberResponse;
import com.smartdevicelink.proxy.rpc.EndAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.GenericResponse;
import com.smartdevicelink.proxy.rpc.GetDTCsResponse;
import com.smartdevicelink.proxy.rpc.GetVehicleData;
import com.smartdevicelink.proxy.rpc.GetVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.Image;
import com.smartdevicelink.proxy.rpc.ListFiles;
import com.smartdevicelink.proxy.rpc.ListFilesResponse;
import com.smartdevicelink.proxy.rpc.MenuParams;
import com.smartdevicelink.proxy.rpc.OnAudioPassThru;
import com.smartdevicelink.proxy.rpc.OnButtonEvent;
import com.smartdevicelink.proxy.rpc.OnButtonPress;
import com.smartdevicelink.proxy.rpc.OnCommand;
import com.smartdevicelink.proxy.rpc.OnDriverDistraction;
import com.smartdevicelink.proxy.rpc.OnHMIStatus;
import com.smartdevicelink.proxy.rpc.OnHashChange;
import com.smartdevicelink.proxy.rpc.OnKeyboardInput;
import com.smartdevicelink.proxy.rpc.OnLanguageChange;
import com.smartdevicelink.proxy.rpc.OnLockScreenStatus;
import com.smartdevicelink.proxy.rpc.OnPermissionsChange;
import com.smartdevicelink.proxy.rpc.OnStreamRPC;
import com.smartdevicelink.proxy.rpc.OnSystemRequest;
import com.smartdevicelink.proxy.rpc.OnTBTClientState;
import com.smartdevicelink.proxy.rpc.OnTouchEvent;
import com.smartdevicelink.proxy.rpc.OnVehicleData;
import com.smartdevicelink.proxy.rpc.PerformAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.PerformInteraction;
import com.smartdevicelink.proxy.rpc.PerformInteractionResponse;
import com.smartdevicelink.proxy.rpc.PutFile;
import com.smartdevicelink.proxy.rpc.PutFileResponse;
import com.smartdevicelink.proxy.rpc.ReadDIDResponse;
import com.smartdevicelink.proxy.rpc.ResetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.ScrollableMessageResponse;
import com.smartdevicelink.proxy.rpc.SendLocationResponse;
import com.smartdevicelink.proxy.rpc.SetAppIconResponse;
import com.smartdevicelink.proxy.rpc.SetDisplayLayoutResponse;
import com.smartdevicelink.proxy.rpc.SetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.SetMediaClockTimerResponse;
import com.smartdevicelink.proxy.rpc.Show;
import com.smartdevicelink.proxy.rpc.ShowConstantTbtResponse;
import com.smartdevicelink.proxy.rpc.ShowResponse;
import com.smartdevicelink.proxy.rpc.SliderResponse;
import com.smartdevicelink.proxy.rpc.SoftButton;
import com.smartdevicelink.proxy.rpc.SpeakResponse;
import com.smartdevicelink.proxy.rpc.StreamRPCResponse;
import com.smartdevicelink.proxy.rpc.SubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.SubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.SystemRequestResponse;
import com.smartdevicelink.proxy.rpc.TTSChunk;
import com.smartdevicelink.proxy.rpc.UnsubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.UpdateTurnListResponse;
import com.smartdevicelink.proxy.rpc.VrHelpItem;
import com.smartdevicelink.proxy.rpc.enums.ButtonName;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.proxy.rpc.enums.HMILevel;
import com.smartdevicelink.proxy.rpc.enums.ImageType;
import com.smartdevicelink.proxy.rpc.enums.InteractionMode;
import com.smartdevicelink.proxy.rpc.enums.LockScreenStatus;
import com.smartdevicelink.proxy.rpc.enums.Result;
import com.smartdevicelink.proxy.rpc.enums.SdlDisconnectedReason;
import com.smartdevicelink.proxy.rpc.enums.SoftButtonType;
import com.smartdevicelink.proxy.rpc.enums.SpeechCapabilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class SdlService extends Service implements IProxyListenerALM {

	private static final String TAG 					= "SDL Service";

	private static final String APP_NAME 				= "Campus Ford";
	private static final String APP_ID 					= "260537589";
	
	private static final String ICON_FILENAME 			= "hello_sdl_icon.png";
	private int iconCorrelationId;

	MediaPlayer mediaPlayer;

	List<String> remoteFiles;
	
	private static final String WELCOME_SHOW 			= "Bem-Vindo ao Aplicativo Ford";
	private static final String WELCOME_SPEAK 			= "Bem Vindo ao Aplicativo Ford";
	
	private static final String TEST_COMMAND_NAME1 		= "Mensagem";
	private static final String TEST_COMMAND_NAME2 		= "Interação";
	private static final String TEST_COMMAND_NAME3 		= "Alerta";
	private static final String TEST_COMMAND_NAME4 		= "Display";

	private static final int SUBMENU_1 = 100;
	private static final int COMMAND_1 = 5;

	private static final int TEST_COMMAND_ID1 			= 1;
	private static final int TEST_COMMAND_ID2 			= 2;
	private static final int TEST_COMMAND_ID3 			= 3;
	private static final int TEST_COMMAND_ID4 			= 4;

	private static final int performCorID = 101;

	private String speed;

    // Conenction management
	private static final int CONNECTION_TIMEOUT = 180 * 1000;
    private Handler mConnectionHandler = new Handler(Looper.getMainLooper());

	// variable used to increment correlation ID for every request sent to SYNC
	public int autoIncCorrId = 0;
	// variable to contain the current state of the service
	private static SdlService instance = null;

	// variable to create and call functions of the SyncProxy
	private SdlProxyALM proxy = null;

	private boolean firstNonHmiNone = true;
	private boolean isVehicleDataSubscribed = false;
	
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
        Log.d(TAG, "onCreate");
		super.onCreate();
		mediaPlayer = new MediaPlayer();
		instance = this;
		remoteFiles = new ArrayList<>();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        startProxy();
		Log.d(TAG, "onStartCommand");
        mConnectionHandler.postDelayed(mCheckConnectionRunnable, CONNECTION_TIMEOUT);

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		disposeSyncProxy();
		Log.d(TAG, "onDestroy");
		instance = null;
		super.onDestroy();
	}

	public void SintonizaRadio(String StreamMusiPath){
		if(mediaPlayer.isPlaying()){
			mediaPlayer.release();
			mediaPlayer = new MediaPlayer();
			mediaPlayer.reset();
		}

		try {
			String streamPath = StreamMusiPath;
			mediaPlayer.setDataSource(streamPath);
			try {
				mediaPlayer.prepare();
			} catch (MalformedURLException ex) {
				throw new RuntimeException("Errro na URL! " + ex);
			} catch (IllegalStateException ex) {
			} catch (IllegalArgumentException ex) {
				throw new RuntimeException("Errro na URL! " + ex);
			}
			mediaPlayer.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public static SdlService getInstance() {
		return instance;
	}

	public SdlProxyALM getProxy() {
		return proxy;
	}

	public void startProxy() {

        Log.i(TAG, "Trying to start proxy");
		if (proxy == null) {
			try {
                Log.i(TAG, "Starting SDL Proxy");
				proxy = new SdlProxyALM(this, APP_NAME, true, APP_ID);
			} catch (SdlException e) {
				e.printStackTrace();
				// error creating proxy, returned proxy = null
				if (proxy == null) {
					stopSelf();
				}
			}
		}
	}

	public void disposeSyncProxy() {
		LockScreenActivity.updateLockScreenStatus(LockScreenStatus.OFF);

		if (proxy != null) {
			try {
				proxy.dispose();
			} catch (SdlException e) {
				e.printStackTrace();
			}
			proxy = null;

		}
		this.firstNonHmiNone = true;
		this.isVehicleDataSubscribed = false;
		
	}

	public void reset() {
		if (proxy != null) {
			try {
				proxy.resetProxy();
				this.firstNonHmiNone = true;
				this.isVehicleDataSubscribed = false;
			} catch (SdlException e1) {
				e1.printStackTrace();
				//something goes wrong, & the proxy returns as null, stop the service.
				// do not want a running service with a null proxy
				if (proxy == null) {
					stopSelf();
				}
			}
		} else {
			startProxy();
		}
	}

	public void subButtons() {
		try {
			proxy.subscribeButton(ButtonName.OK, autoIncCorrId++);
			//proxy.subscribeButton(ButtonName.SEEKLEFT, autoIncCorrId++);
			//proxy.subscribeButton(ButtonName.SEEKRIGHT, autoIncCorrId++);
			proxy.subscribeButton(ButtonName.TUNEUP, autoIncCorrId++);
			proxy.subscribeButton(ButtonName.TUNEDOWN, autoIncCorrId++);
		} catch (SdlException e) {
		}
	}

	/**
	 * Will show a sample test message on screen as well as speak a sample test message
	 */
	public void showscrollablemessage(){
		try {
			proxy.scrollablemessage("O entrelaçamento quântico (ou emaranhamento quântico, como é mais conhecido na comunidade científica) é +" +
					"um fenômeno da mecânica quântica que permite que dois ou mais objetos estejam de alguma forma tão ligados que um objeto não +" +
					"possa ser corretamente descrito sem que a sua contra-parte seja mencionada - mesmo que os objetos possam estar espacialmente +" +
					"separados por milhões de anos-luz.",30000,null, autoIncCorrId++);
		} catch (SdlException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  Add commands for the app on SDL.
	 */
	public void sendCommands1(){
		AddCommand command;
		MenuParams params = new MenuParams();
		params.setMenuName(TEST_COMMAND_NAME1);
		command = new AddCommand();
		command.setCmdID(TEST_COMMAND_ID1);
		command.setMenuParams(params);
		command.setVrCommands(Arrays.asList(new String[]{TEST_COMMAND_NAME1}));
		sendRpcRequest(command);
	}
	public void sendCommands2(){
		AddCommand command;
		MenuParams params = new MenuParams();
		params.setMenuName(TEST_COMMAND_NAME2);
		command = new AddCommand();
		command.setCmdID(TEST_COMMAND_ID2);
		command.setMenuParams(params);
		command.setVrCommands(Arrays.asList(new String[]{TEST_COMMAND_NAME2}));
		sendRpcRequest(command);
	}
	public void sendCommands3(){
		AddCommand command;
		MenuParams params = new MenuParams();
		params.setMenuName(TEST_COMMAND_NAME3);
		command = new AddCommand();
		command.setCmdID(TEST_COMMAND_ID3);
		command.setMenuParams(params);
		command.setVrCommands(Arrays.asList(new String[]{TEST_COMMAND_NAME3}));
		sendRpcRequest(command);
	}
	public void sendCommands4(){
		AddCommand command;
		MenuParams params = new MenuParams();
		params.setMenuName(TEST_COMMAND_NAME4);
		command = new AddCommand();
		command.setCmdID(TEST_COMMAND_ID4);
		command.setMenuParams(params);
		command.setVrCommands(Arrays.asList(new String[]{TEST_COMMAND_NAME4}));
		sendRpcRequest(command);
	}

	public void addCommandsSub() {

		AddSubMenu submenu = new AddSubMenu();
		submenu.setMenuName("SubMenu");
		submenu.setPosition(0);
		submenu.setMenuID(SUBMENU_1);
		submenu.setCorrelationID(autoIncCorrId++);

		try {
			proxy.sendRPCRequest(submenu);
		} catch (SdlException e) {
			Log.i(TAG,"sync exception");
			e.printStackTrace();
		}
	}

	/**
	 * Sends an RPC Request to the connected head unit. Automatically adds a correlation id.
	 * @param request
	 */
	private void sendRpcRequest(RPCRequest request){
		request.setCorrelationID(autoIncCorrId++);
		try {
			proxy.sendRPCRequest(request);
		} catch (SdlException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Sends the app icon through the uploadImage method with correct params
	 * @throws SdlException
	 */
	private void sendIcon() throws SdlException {
		iconCorrelationId = autoIncCorrId++;
		uploadImage(R.drawable.ic_launcher, ICON_FILENAME, iconCorrelationId, true);
	}
	
	/**
	 * This method will help upload an image to the head unit
	 * @param resource the R.drawable.__ value of the image you wish to send
	 * @param imageName the filename that will be used to reference this image
	 * @param correlationId the correlation id to be used with this request. Helpful for monitoring putfileresponses
	 * @param isPersistent tell the system if the file should stay or be cleared out after connection.
	 */
	private void uploadImage(int resource, String imageName,int correlationId, boolean isPersistent){
		PutFile putFile = new PutFile();
		putFile.setFileType(FileType.GRAPHIC_PNG);
		putFile.setSdlFileName(imageName);
		putFile.setCorrelationID(correlationId);
		putFile.setPersistentFile(isPersistent);
		putFile.setSystemFile(false);
		putFile.setBulkData(contentsOfResource(resource));

		try {
			proxy.sendRPCRequest(putFile);
		} catch (SdlException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Helper method to take resource files and turn them into byte arrays
	 * @param resource Resource file id.
	 * @return Resulting byte array.
	 */
	private byte[] contentsOfResource(int resource) {
		InputStream is = null;
		try {
			is = getResources().openRawResource(resource);
			ByteArrayOutputStream os = new ByteArrayOutputStream(is.available());
			final int bufferSize = 4096;
			final byte[] buffer = new byte[bufferSize];
			int available;
			while ((available = is.read(buffer)) >= 0) {
				os.write(buffer, 0, available);
			}
			return os.toByteArray();
		} catch (IOException e) {
			Log.w("SDL Service", "Can't read icon file", e);
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onProxyClosed(String info, Exception e, SdlDisconnectedReason reason) {

		if(!(e instanceof SdlException)){
			Log.v(TAG, "reset proxy in onproxy closed");
			reset();
		}
		else if ((((SdlException) e).getSdlExceptionCause() != SdlExceptionCause.SDL_PROXY_CYCLED))
		{
			if (((SdlException) e).getSdlExceptionCause() != SdlExceptionCause.BLUETOOTH_DISABLED) 
			{
				Log.v(TAG, "reset proxy in onproxy closed");
				reset();
			}
		}


		stopSelf();
	}

	@Override
	public void onOnHMIStatus(OnHMIStatus notification) {
		Log.d(TAG, notification.getHmiLevel().toString());
		if(notification.getHmiLevel().equals(HMILevel.HMI_FULL)){
			if (notification.getFirstRun()) {
				// send welcome message if applicable
				performWelcomeMessage();
			}
		}

		if(!notification.getHmiLevel().equals(HMILevel.HMI_NONE)
				&& firstNonHmiNone){
			sampleCreateChoiceSet();
			addCommandsSub();
			sendCommands1();
			sendCommands2();
			sendCommands3();
			sendCommands4();
			subButtons();
			SintonizaRadio("http://mixaac.crossradio.com.br:1100/live");

			firstNonHmiNone = false;
			
			// Other app setup (SubMenu, CreateChoiceSet, etc.) would go here
		}else{
			//We have HMI_NONE
			if(notification.getFirstRun()){
				uploadImages();
                mConnectionHandler.removeCallbacksAndMessages(null);
			}
		}
		
	}

	public void sampleCreateChoiceSet()
	{
		int corrId = autoIncCorrId++;

		Vector<Choice> commands = new Vector<Choice>();
		Choice one = new Choice();
		one.setChoiceID(99);
		one.setMenuName("Um");
		Vector<String> vrCommands = new Vector<String>();
		vrCommands.add("Um");
		vrCommands.add("1");
		vrCommands.add("numero um");
		one.setVrCommands(vrCommands);

		//icon shown to the left on a choice set item
		Image image1 = new Image();
		image1.setImageType(ImageType.STATIC);
		image1.setValue("0x89");
		one.setImage(image1);
		commands.add(one);

		Choice two = new Choice();
		two.setChoiceID(100);
		two.setMenuName("Dois");
		Vector<String> vrCommands2 = new Vector<String>();
		vrCommands2.add("Dois");
		vrCommands2.add("2");
		vrCommands2.add("numero dois");
		two.setVrCommands(vrCommands2);

		Image image2 = new Image();
		image2.setImageType(ImageType.STATIC);
		image2.setValue("0x89");
		two.setImage(image2);
		commands.add(two);

		//Build Request and send to proxy object:
		CreateInteractionChoiceSet msg = new CreateInteractionChoiceSet();
		msg.setCorrelationID(corrId);
		int choiceSetID = 101;
		msg.setInteractionChoiceSetID(choiceSetID);
		msg.setChoiceSet(commands);

		try {
			proxy.sendRPCRequest(msg);
		} catch (SdlException e) {
			Log.i(TAG,"sync exception Choice");
			e.printStackTrace();
		}
	}

	public void samplePerformInteraction()
	{
		/******Prerequisite CreateInteractionChoiceSet Occurs Prior to PerformInteraction*******/
		//Build Request and send to proxy object:
		int corrId = performCorID;
		PerformInteraction msg = new PerformInteraction();
		msg.setCorrelationID(corrId);
		msg.setInitialText("Opções: ");

		Vector<TTSChunk> chunksinit = new Vector<TTSChunk>();
		TTSChunk chunkI = new TTSChunk();
		chunkI.setText("Por favor, escolha uma opção");
		chunkI.setType(SpeechCapabilities.TEXT);
		chunksinit.add(chunkI);

		msg.setInitialPrompt(chunksinit);
		msg.setInteractionMode(InteractionMode.BOTH);

		Vector<Integer> choiceSetIDs = new Vector<Integer>();
		choiceSetIDs.add(101); // match the ID used in CreateInteractionChoiceSet
		msg.setInteractionChoiceSetIDList(choiceSetIDs);

		Vector<TTSChunk> chunksAjuda = new Vector<TTSChunk>();
		TTSChunk chunkA = new TTSChunk();
		chunkA.setText("Um ou Dois");
		chunkA.setType(SpeechCapabilities.TEXT);
		chunksAjuda.add(chunkA);

		Vector<TTSChunk> chunksTimeout = new Vector<TTSChunk>();
		TTSChunk chunkT = new TTSChunk();
		chunkT.setText("Timeout");
		chunkT.setType(SpeechCapabilities.TEXT);
		chunksTimeout.add(chunkT);

		msg.setHelpPrompt(chunksAjuda);
		msg.setTimeoutPrompt(chunksTimeout);
		msg.setTimeout(50000); //min value 5000, max 10000 milliseconds

		Vector<VrHelpItem> vrHelpItems = new Vector<VrHelpItem>();
		VrHelpItem item1 = new VrHelpItem();
		item1.setText("Um");
		item1.setPosition(1);
		VrHelpItem item2 = new VrHelpItem();
		item2.setText("Dois");
		item2.setPosition(2);

		vrHelpItems.add(item1);
		vrHelpItems.add(item2);
		msg.setVrHelp(vrHelpItems);

		try{
			proxy.sendRPCRequest(msg);
		} catch (SdlException e) {
			Log.i(TAG,"sync exception");
			e.printStackTrace();
		}
	}


	/**
	 * Will show a sample welcome message on screen as well as speak a sample welcome message
	 */
	private void performWelcomeMessage(){
		try {
			//Set the welcome message on screen

			Show newShow = new Show();
			newShow.setCorrelationID(autoIncCorrId++);
			Image newImage = new Image();
			newImage.setValue(ICON_FILENAME);
			newImage.setImageType(ImageType.DYNAMIC);
			newShow.setGraphic(newImage);
			newShow.setMainField1(APP_NAME);
			newShow.setMainField2(WELCOME_SHOW);

			SoftButton newSB = new SoftButton();
			newSB.setSoftButtonID(1);

			newSB.setType(SoftButtonType.SBT_BOTH);

			Image newImg = new Image();
			newImg.setValue(ICON_FILENAME);
			newImg.setImageType(ImageType.DYNAMIC);
			newSB.setImage(newImg);
			newSB.setText("Home");
			Vector<SoftButton> softButtons = new Vector<SoftButton>();
			softButtons.add(newSB);
			newShow.setSoftButtons(softButtons);

			proxy.sendRPCRequest(newShow);

			//Say the welcome message
			proxy.speak(WELCOME_SPEAK, autoIncCorrId++);
			
		} catch (SdlException e) {
			e.printStackTrace();
		}
		
	}

	public void sampleGetVehicleData()
	{
		//Build Request and send to proxy object:
		int corrId = autoIncCorrId++;
		GetVehicleData msg = new GetVehicleData();
		msg.setCorrelationID(corrId);
		//Location functional group
		msg.setSpeed(true);
		msg.setGps(false);

		//VechicleInfo functional group
		msg.setFuelLevel(false);
		msg.setFuelLevel_State(false);
		msg.setInstantFuelConsumption(false);
		msg.setExternalTemperature(false);
		msg.setTirePressure(false);
		msg.setOdometer(false);
		msg.setVin(false);

		//DrivingCharacteristics functional group
		msg.setBeltStatus(false);
		msg.setDriverBraking(false);
		msg.setPrndl(false);
		msg.setRpm(false);

		try{
			proxy.sendRPCRequest(msg);
		}
		catch (SdlException e) {
			Log.i(TAG,"sync exception Vhicledata");
			e.printStackTrace();
		}
	}


	/**
	 *  Requests list of images to SDL, and uploads images that are missing.
	 */
	private void uploadImages(){
		ListFiles listFiles = new ListFiles();
		this.sendRpcRequest(listFiles);
		
	}

	@Override
	public void onListFilesResponse(ListFilesResponse response) {
		Log.i(TAG, "onListFilesResponse from SDL ");
		if(response.getSuccess()){
			remoteFiles = response.getFilenames();
		}
		
		// Check the mutable set for the AppIcon
	    // If not present, upload the image
		if(remoteFiles== null || !remoteFiles.contains(SdlService.ICON_FILENAME)){
			try {
				sendIcon();
			} catch (SdlException e) {
				e.printStackTrace();
			}
		}else{
			// If the file is already present, send the SetAppIcon request
			try {
				proxy.setappicon(ICON_FILENAME, autoIncCorrId++);
			} catch (SdlException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onPutFileResponse(PutFileResponse response) {
		Log.i(TAG, "onPutFileResponse from SDL");
		if(response.getCorrelationID() == iconCorrelationId){ //If we have successfully uploaded our icon, we want to set it
			try {
				proxy.setappicon(ICON_FILENAME, autoIncCorrId++);
			} catch (SdlException e) {
				e.printStackTrace();
			}
		}

	}
	
	@Override
	public void onOnLockScreenNotification(OnLockScreenStatus notification) {
		LockScreenActivity.updateLockScreenStatus(notification.getShowLockScreen());
	}

	@Override
	public void onOnCommand(OnCommand notification){
		Integer id = notification.getCmdID();
		if(id != null){
			switch(id){
			case TEST_COMMAND_ID1:
				showscrollablemessage();
				break;
			case TEST_COMMAND_ID2:
				samplePerformInteraction();
				break;
			case TEST_COMMAND_ID3:
				try {
					proxy.alert("Olá, este é o Aplicativo Ford","Olá", "Aplicativo Ford",null,true,5000,null,autoIncCorrId++);
				} catch (SdlException e) {
					e.printStackTrace();
				}
				break;
			case TEST_COMMAND_ID4:
				try {
					proxy.setdisplaylayout("TEXTBUTTONS_WITH_GRAPHIC", autoIncCorrId++);
				} catch (SdlException e) {
					e.printStackTrace();
				}
				break;
			case COMMAND_1:
				sampleGetVehicleData();
				break;
			}

		}
	}

	/**
	 *  Callback method that runs when the add command response is received from SDL.
	 */
	@Override
	public void onAddCommandResponse(AddCommandResponse response) {
		Log.i(TAG, "AddCommand response from SDL: " + response.getResultCode().name());

	}

	
	/*  Vehicle Data   */
	
	
	@Override
	public void onOnPermissionsChange(OnPermissionsChange notification) {
		Log.i(TAG, "Permision changed: " + notification);
	}
		
	@Override
	public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse response) {
		if(response.getSuccess()){
			Log.i(TAG, "Subscribed to vehicle data");
			this.isVehicleDataSubscribed = true;
		}
	}
	
	@Override
	public void onOnVehicleData(OnVehicleData notification) {
		Log.i(TAG, "Vehicle data notification from SDL");
		//TODO Put your vehicle data code here
		//ie, notification.getSpeed().

	}
	
	/**
	 * Rest of the SDL callbacks from the head unit
	 */
	
	@Override
	public void onAddSubMenuResponse(AddSubMenuResponse response) {
        Log.i(TAG, "AddSubMenu response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
		boolean bSuccess = response.getSuccess();

		// Create and build an AddCommand RPC
		AddCommand msg = new AddCommand();
		msg.setCmdID(COMMAND_1);
		String menuName = "Velocidade";
		MenuParams menuParams = new MenuParams();
		menuParams.setMenuName(menuName);

		// Set the parent ID to the submenu that was added only if the response was SUCCESS
		if (bSuccess) {
			menuParams.setParentID(SUBMENU_1);
		}
		msg.setMenuParams(menuParams);

		// Build voice recognition commands
		Vector<String> vrCommands = new Vector<String>();
		vrCommands.add("Velocidade");
		vrCommands.add("Velocidade do veiculo");
		msg.setVrCommands(vrCommands);

		// Set the correlation ID
		int correlationId = autoIncCorrId++;
		msg.setCorrelationID(correlationId);

		// Send to proxy
		try {
			proxy.sendRPCRequest(msg);
		} catch (SdlException e) {
			Log.i(TAG,"sync exception");
			e.printStackTrace();
		}

	}

	@Override
	public void onCreateInteractionChoiceSetResponse(CreateInteractionChoiceSetResponse response) {
        Log.i(TAG, "CreateInteractionChoiceSet response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}

	@Override
	public void onAlertResponse(AlertResponse response) {
        Log.i(TAG, "Alert response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	@Override
	public void onDeleteCommandResponse(DeleteCommandResponse response) {
        Log.i(TAG, "DeleteCommand response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	@Override
	public void onDeleteInteractionChoiceSetResponse(DeleteInteractionChoiceSetResponse response) {
        Log.i(TAG, "DeleteInteractionChoiceSet response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	@Override
	public void onDeleteSubMenuResponse(DeleteSubMenuResponse response) {
        Log.i(TAG, "DeleteSubMenu response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	@Override
	public void onPerformInteractionResponse(PerformInteractionResponse response) {
        Log.i(TAG, "PerformInteraction response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
		Log.d(TAG,"S onPerformInteractionResponse" + response.getResultCode() + response.getCorrelationID() + " " + response.getChoiceID());
		Log.d(TAG, response.getInfo());

		if(response.getCorrelationID() ==  performCorID) {
			switch(response.getChoiceID())
			{
				case 99:
					Log.d(TAG, "Case 99" );
					break;
				case 100:
					Log.d(TAG, "Case 100" );
					break;
				default:
					return;
			}
		}
	}

	@Override
	public void onResetGlobalPropertiesResponse(
			ResetGlobalPropertiesResponse response) {
        Log.i(TAG, "ResetGlobalProperties response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	@Override
	public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse response) {
        Log.i(TAG, "SetGlobalProperties response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	@Override
	public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse response) {
        Log.i(TAG, "SetMediaClockTimer response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	@Override
	public void onShowResponse(ShowResponse response) {
        Log.i(TAG, "Show response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	@Override
	public void onSpeakResponse(SpeakResponse response) {
        Log.i(TAG, "SpeakCommand response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	@Override
	public void onOnButtonEvent(OnButtonEvent notification) {
        Log.i(TAG, "OnButtonEvent notification from SDL: " + notification);
	}

	@Override
	public void onOnButtonPress(OnButtonPress notification) {
        Log.i(TAG, "OnButtonPress notification from SDL: " + notification);
		switch (notification.getButtonName()) {
			case CUSTOM_BUTTON:
					switch (notification.getCustomButtonName()) {
						case 1: // rss icon was pressed on main screen
							Log.i(TAG, "start was pressed");
							try {
								proxy.setdisplaylayout("TILES_WITH_GRAPHIC", autoIncCorrId++);
							} catch (SdlException e) {
								Log.i(TAG,"sync exception");
								e.printStackTrace();
							}
							break;
					}
				break;
			case OK:
				SintonizaRadio("http://mixaac.crossradio.com.br:1100/live");
				//mediaPlayer.release();
				//mediaPlayer = new MediaPlayer();
				//mediaPlayer.reset();
				break;
		}
	}

	@Override
	public void onSubscribeButtonResponse(SubscribeButtonResponse response) {
        Log.i(TAG, "SubscribeButton response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	@Override
	public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse response) {
        Log.i(TAG, "UnsubscribeButton response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}


	@Override
	public void onOnTBTClientState(OnTBTClientState notification) {
        Log.i(TAG, "OnTBTClientState notification from SDL: " + notification);
	}

	@Override
	public void onUnsubscribeVehicleDataResponse(
			UnsubscribeVehicleDataResponse response) {
        Log.i(TAG, "UnsubscribeVehicleData response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}

	@Override
	public void onGetVehicleDataResponse(GetVehicleDataResponse response) {
		if (response.getResultCode() == Result.DISALLOWED) {
			Log.i(TAG, "onGetVehicleDataResponse read disallowed");
			try {
				proxy.show("VehicleData Policy", "Disallowed", null, autoIncCorrId++);
			} catch (SdlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		} else if (response.getResultCode() == Result.USER_DISALLOWED) {
			Log.i(TAG, "VehicleData read user disallowed");
			try {
				proxy.show("VehicleData User", "Disallowed", null, autoIncCorrId++);
			} catch (SdlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}

		try {
			if (response.getSpeed() != null) {
				speed = response.getSpeed().toString();
				try {
					proxy.show("Velocidade: ", speed.toString(), null, autoIncCorrId++);
				} catch (SdlException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				speed = "--";
			}
		} catch (Exception e) {
			Log.e(TAG, "onvehicledata error" + e);
			e.printStackTrace();
		}
	}

	@Override
	public void onReadDIDResponse(ReadDIDResponse response) {
        Log.i(TAG, "ReadDID response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}

	@Override
	public void onGetDTCsResponse(GetDTCsResponse response) {
        Log.i(TAG, "GetDTCs response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}


	@Override
	public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse response) {
        Log.i(TAG, "PerformAudioPassThru response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}

	@Override
	public void onEndAudioPassThruResponse(EndAudioPassThruResponse response) {
        Log.i(TAG, "EndAudioPassThru response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}

	@Override
	public void onOnAudioPassThru(OnAudioPassThru notification) {
        Log.i(TAG, "OnAudioPassThru notification from SDL: " + notification );

	}

	@Override
	public void onDeleteFileResponse(DeleteFileResponse response) {
        Log.i(TAG, "DeleteFile response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}

	@Override
	public void onSetAppIconResponse(SetAppIconResponse response) {
        Log.i(TAG, "SetAppIcon response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}

	@Override
	public void onScrollableMessageResponse(ScrollableMessageResponse response) {
        Log.i(TAG, "ScrollableMessage response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}

	@Override
	public void onChangeRegistrationResponse(ChangeRegistrationResponse response) {
        Log.i(TAG, "ChangeRegistration response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}

	@Override
	public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse response) {
        Log.i(TAG, "SetDisplayLayout response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}

	@Override
	public void onOnLanguageChange(OnLanguageChange notification) {
        Log.i(TAG, "OnLanguageChange notification from SDL: " + notification);

	}

	@Override
	public void onSliderResponse(SliderResponse response) {
        Log.i(TAG, "Slider response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}


	@Override
	public void onOnHashChange(OnHashChange notification) {
        Log.i(TAG, "OnHashChange notification from SDL: " + notification);

	}

	@Override
	public void onOnSystemRequest(OnSystemRequest notification) {
        Log.i(TAG, "OnSystemRequest notification from SDL: " + notification);

	}

	@Override
	public void onSystemRequestResponse(SystemRequestResponse response) {
        Log.i(TAG, "SystemRequest response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}

	@Override
	public void onOnKeyboardInput(OnKeyboardInput notification) {
        Log.i(TAG, "OnKeyboardInput notification from SDL: " + notification);

	}

	@Override
	public void onOnTouchEvent(OnTouchEvent notification) {
        Log.i(TAG, "OnTouchEvent notification from SDL: " + notification);

	}

	@Override
	public void onDiagnosticMessageResponse(DiagnosticMessageResponse response) {
        Log.i(TAG, "DiagnosticMessage response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}

	@Override
	public void onOnStreamRPC(OnStreamRPC notification) {
        Log.i(TAG, "OnStreamRPC notification from SDL: " + notification);

	}

	@Override
	public void onStreamRPCResponse(StreamRPCResponse response) {
        Log.i(TAG, "StreamRPC response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}

	@Override
	public void onDialNumberResponse(DialNumberResponse response) {
        Log.i(TAG, "DialNumber response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}

	@Override
	public void onSendLocationResponse(SendLocationResponse response) {
        Log.i(TAG, "SendLocation response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}

	@Override
	public void onServiceEnded(OnServiceEnded serviceEnded) {

	}

	@Override
	public void onServiceNACKed(OnServiceNACKed serviceNACKed) {

	}

	@Override
	public void onShowConstantTbtResponse(ShowConstantTbtResponse response) {
        Log.i(TAG, "ShowConstantTbt response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}

	@Override
	public void onAlertManeuverResponse(AlertManeuverResponse response) {
        Log.i(TAG, "AlertManeuver response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}

	@Override
	public void onUpdateTurnListResponse(UpdateTurnListResponse response) {
        Log.i(TAG, "UpdateTurnList response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());

	}

	@Override
	public void onServiceDataACK() {

	}
	
	@Override
	public void onOnDriverDistraction(OnDriverDistraction notification) {
		// Some RPCs (depending on region) cannot be sent when driver distraction is active.
	}

	@Override
	public void onError(String info, Exception e) {
	}

	@Override
	public void onGenericResponse(GenericResponse response) {
        Log.i(TAG, "Generic response from SDL: " + response.getResultCode().name() + " Info: " + response.getInfo());
	}

	private Runnable mCheckConnectionRunnable = new Runnable() {
		@Override
		public void run() {
            stopSelf();
		}
	};

}
