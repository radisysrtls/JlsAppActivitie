package com.jio.rtlsappfull.ui.main;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthNr;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jio.rtlsappfull.R;
import com.jio.rtlsappfull.database.db.DBManager;
import com.jio.rtlsappfull.internal.JiotFetchCustomLatLng;
import com.jio.rtlsappfull.internal.JiotFetchReceiver;
import com.jio.rtlsappfull.internal.JiotMainActivity;
import com.jio.rtlsappfull.log.JiotSdkFileLogger;
import com.jio.rtlsappfull.model.GetLocationAPIResponse;
import com.jio.rtlsappfull.model.JiotCustomCellData;
import com.jio.rtlsappfull.model.JiotRtlsBleRecord;
import com.jio.rtlsappfull.model.JiotRtlsWifiData;
import com.jio.rtlsappfull.utils.JiotUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jio.rtlsappfull.config.Config.LOCATION_DEV_URL;
import static com.jio.rtlsappfull.config.Config.LOCATION_PREPROD_URL;
import static com.jio.rtlsappfull.config.Config.LOCATION_PROD_URL;
import static com.jio.rtlsappfull.config.Config.LOCATION_SIT_URL;
import static com.jio.rtlsappfull.config.Config.isJioVm;
import static com.jio.rtlsappfull.config.Config.RADISYS_GEOLOCATE_API_URL_ALL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */

@RequiresApi(api = Build.VERSION_CODES.R)
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private PageViewModel pageViewModel;
    int index = 1;
    int MCC = 0;
    int MNC = 0;
    View root;
    TextView mcc_id;
    TextView mnc_id;
    TextView cellinfo_id;
    TextView radiotype_id;
    TextView neighbor_cellinfo_id;
    TextView ter_cellinfo_id;
    TextView signal_id;
    int SIGNAL_STRENGTH = 1;
    long CELLID = 1;
    String RADIOTYPE = "gsm";
    int LAC;
    JiotFetchCustomLatLng m_JiotFetchCustomLatLng;
    List<JiotCustomCellData> m_CustomCellDataAll;

    TextView m_secondary_radiotype_id;
    TextView m_secondary_signal_id;

    TextView m_ter_radiotype_id;
    TextView m_ter_signal_id;

    TextView neigh3_radiotype_id;
    TextView neigh3_signal_id;
    TextView neigh3_cellinfo_id;

    TextView neigh4_cellinfo_id;
    TextView neigh4_radiotype_id;
    TextView neigh4_signal_id;

    private TextView tac;
    private TextView frequency;
    private int TAC;
    private int FREQUENCY;

    public static JiotSdkFileLogger m_jiotSdkFileLoggerInstance = null;
    List<JiotRtlsWifiData> m_jiotRtlsWifiDataList;
    JiotRtlsWifiData mSelfWifi;
    public BluetoothAdapter m_bluetoothAdapter;
    public BluetoothManager m_bluetoothManager;
    public BluetoothLeScanner m_bluetoothLeScanner;
    public List<JiotRtlsBleRecord> m_jiotRtlsBleRecordList;
    public Handler m_handler;
    public static boolean m_isScanning = false;
    public static final String BLETAG = "BLEOP";
    public static ProgressDialog m_progressDialogWait;
    public Handler m_waitDailogHandler;
    public MaterialAlertDialogBuilder m_alertDialog = null;
    private long mRtlsFetchTimeStartInMs = 0;
    private long mRtlsFetchTimeEndInMs = 0;
    private HashMap<String, LatLng> m_servIdLocation;
    private DBManager mDbManager;

    public void showProgressWaitDialog() {
        m_progressDialogWait.setMessage(getResources().getString(R.string.ble_wait_msg));
        m_progressDialogWait.setIndeterminate(true);
        m_progressDialogWait.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_progressDialogWait.show();
    }

    public void dimissProgressWaitDialog() {
        m_progressDialogWait.dismiss();
    }

    public void handleBleScan() {
        ((JiotMainActivity) getActivity()).m_first_fetch = false;
//        handleProgressWaitDialog();
        if (m_bluetoothAdapter.isEnabled()) {
            Log.d(BLETAG, "start handleBleScan");
            m_handler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    stopScan();
                    getMainData();
                }
            }, JiotUtils.RTLS_SCAN_PERIOD);
//            startScan();
        } else {
            Log.d(BLETAG, "BT OFF handleBleScan");
            getMainData();
        }
    }

    public void refreshView() {
        pageViewModel.getText().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Log.d("REFRESHVIEW", "refreshView");
                mcc_id.setText("MCC: " + MCC);
                mnc_id.setText("MNC: " + MNC);
                cellinfo_id.setText("CellId: " + CELLID);
                signal_id.setText("Signal Strength: " + SIGNAL_STRENGTH);
                radiotype_id.setText("Radio Type: " + RADIOTYPE);
                frequency.setText("Frequency: " + FREQUENCY);
                tac.setText("TAC: " + TAC);
                setSecondaryCellIds();
            }
        });
    }

    public void createAlertDialog() {
        m_alertDialog = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme)
                .setTitle(getResources().getString(R.string.jiousername_net_alert))
                .setMessage(getResources().getString(R.string.jiopermissions_main_block))
                .setPositiveButton(getResources().getString(R.string.jiousername_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    public void showAlertDialog() {
        m_alertDialog.show();
    }

    public void getMainData() {
        getCellInfo();
        if (JiotUtils.jiotisNetworkEnabled(getContext()) == true) {
            getWifiInfo();
            fetchServerLocation();
        } else {
            createAlertDialog();
            showAlertDialog();
        }
        refreshView();
    }


    public BroadcastReceiver m_fetchLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            m_jiotRtlsBleRecordList.clear();
            Log.d("m_fetchLocationReceiver", " m_fetchLocationReceiver");
            handleBleScan();
        }
    };


    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDestroy() {
        Log.d("CELLID", "onDestroy");
        super.onDestroy();
        try {
            getContext().unregisterReceiver(m_fetchLocationReceiver);
            m_fetchLocationReceiver = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
        m_CustomCellDataAll = new ArrayList();

        m_jiotRtlsWifiDataList = new ArrayList();
        m_bluetoothManager =
                (BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        m_bluetoothAdapter = m_bluetoothManager.getAdapter();
        m_bluetoothLeScanner = m_bluetoothAdapter.getBluetoothLeScanner();
        m_jiotRtlsBleRecordList = new ArrayList();
        m_handler = new Handler();
        m_waitDailogHandler = new Handler();
        m_progressDialogWait = new ProgressDialog(getContext());
        m_jiotSdkFileLoggerInstance = JiotSdkFileLogger.JiotGetFileLoggerInstance(getContext());
        IntentFilter locationFetchFilter = new IntentFilter("com.jio.fetchLocation");
        getContext().registerReceiver(m_fetchLocationReceiver, locationFetchFilter);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (index == 1) {
            root = inflater.inflate(R.layout.fragment_cellinfo, container, false);
            Log.d("CALLINFO", "calling");
            mcc_id = root.findViewById(R.id.mcc_id);
            mnc_id = root.findViewById(R.id.mnc_id);
            signal_id = root.findViewById(R.id.signal_id);
            cellinfo_id = root.findViewById(R.id.cellinfo_id);
            radiotype_id = root.findViewById(R.id.radiotype_id);

            neighbor_cellinfo_id = root.findViewById(R.id.neighbor_cellinfo_id);
            m_secondary_radiotype_id = root.findViewById(R.id.secondary_radiotype_id);
            m_secondary_signal_id = root.findViewById(R.id.secondary_signal_id);

            ter_cellinfo_id = root.findViewById(R.id.ter_cellinfo_id);
            m_ter_radiotype_id = root.findViewById(R.id.ter_radiotype_id);
            m_ter_signal_id = root.findViewById(R.id.ter_signal_id);


            neigh3_radiotype_id = root.findViewById(R.id.neigh3_radiotype_id);
            neigh3_signal_id = root.findViewById((R.id.neigh3_signal_id));
            neigh3_cellinfo_id = root.findViewById(R.id.neigh3_cellinfo_id);

            tac = root.findViewById(R.id.tac);
            frequency = root.findViewById(R.id.frequency);

            neigh4_cellinfo_id = root.findViewById(R.id.neigh4_cellinfo_id);
            neigh4_radiotype_id = root.findViewById(R.id.neigh4_radiotype_id);
            neigh4_signal_id = root.findViewById(R.id.neigh4_signal_id);
            refreshView();
            m_servIdLocation = new HashMap();
        }
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) == false) {
            Log.d("NOBLE", "NOBLE QUIT");
            Toast.makeText(getContext(), getResources().getString(R.string.noble_support).toString(), Toast.LENGTH_LONG).show();
            getActivity().finishAffinity();
        } else {
            m_jiotRtlsBleRecordList.clear();
            Log.d("BLETHERE", "BLE SUPPORTED");
        }
        mDbManager = new DBManager(getActivity());
        handleBleScan();
    }

    public void fetchServerLocation() {
        String url = "";
        if (isJioVm) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
            String serverName = sharedPreferences.getString("server_name", null);
            if (serverName.equalsIgnoreCase("Prod")) {
                url = LOCATION_PROD_URL;
            } else if (serverName.equalsIgnoreCase("Sit")) {
                url = LOCATION_SIT_URL;
            } else if (serverName.equalsIgnoreCase("Dev")) {
                url = LOCATION_DEV_URL;
            } else if (serverName.equalsIgnoreCase("Preprod")) {
                url = LOCATION_PREPROD_URL;
            }
        } else {
            url = RADISYS_GEOLOCATE_API_URL_ALL + JiotUtils.jiotgetRtlsToken(getContext());
        }

        Log.d("COMPLETEURL = ", url);
        makeAPICall(url);
    }

    public void getMccMnc(String networkOperator) {
        if (networkOperator != null) {
            if (networkOperator.length() > 3) {
                MCC = Integer.parseInt(networkOperator.substring(0, 3));
                MNC = Integer.parseInt(networkOperator.substring(3));
            }
            Log.d("MCCMNC", MCC + "::" + MNC);
        }
    }

    public void setSecondaryCellIds() {
        boolean secondaryDone1 = false;
        boolean secondaryDone2 = false;
        boolean secondaryDone3 = false;
        boolean secondaryDone4 = false;

        for (JiotCustomCellData cellData : m_CustomCellDataAll) {
            if (cellData.isM_isPrimary() == false) {
                if (secondaryDone1 == false) {
                    m_secondary_radiotype_id.setText("Radio Type: " + cellData.getM_radioType());
                    neighbor_cellinfo_id.setText("Neighbour CellId: " + String.valueOf(cellData.getM_cellId()));
                    m_secondary_signal_id.setText("Signal Strength: " + String.valueOf(cellData.getM_signalStrength()));
                    secondaryDone1 = true;
                } else if (secondaryDone2 == false) {
                    ter_cellinfo_id.setText("Neighbour CellId: " + String.valueOf(cellData.getM_cellId()));
                    m_ter_radiotype_id.setText("Radio Type: " + cellData.getM_radioType());
                    m_ter_signal_id.setText("Signal Strength: " + String.valueOf(cellData.getM_signalStrength()));
                    secondaryDone2 = true;
                } else if (secondaryDone3 == false) {
                    neigh3_cellinfo_id.setText("Neighbour CellId: " + String.valueOf(cellData.getM_cellId()));
                    neigh3_radiotype_id.setText("Radio Type: " + cellData.getM_radioType());
                    neigh3_signal_id.setText("Signal Strength: " + String.valueOf(cellData.getM_signalStrength()));
                    secondaryDone3 = true;
                } else if (secondaryDone4 == false) {
                    neigh4_cellinfo_id.setText("Neighbour CellId: " + String.valueOf(cellData.getM_cellId()));
                    neigh4_radiotype_id.setText("Radio Type: " + cellData.getM_radioType());
                    neigh4_signal_id.setText("Signal Strength: " + String.valueOf(cellData.getM_signalStrength()));
                    secondaryDone4 = true;
                }
            }
        }
    }

    public void setPrimaryCellIds() {
        for (JiotCustomCellData cellData : m_CustomCellDataAll) {
            if (cellData.isM_isPrimary()) {
                Long cellId = cellData.getM_cellId();
                if (cellId > 0)
                    CELLID = cellId;
                else
                    continue;
                int rssi = cellData.getM_signalStrength();
                if (rssi >= -150 && rssi <= 0)
                    SIGNAL_STRENGTH = rssi;
                else
                    continue;
                LAC = cellData.getM_locationAreaCode();
                RADIOTYPE = cellData.getM_radioType();
                Log.d("PRIMARY", " " + RADIOTYPE);
                if (RADIOTYPE.equalsIgnoreCase("wcdma") || RADIOTYPE.equalsIgnoreCase("lte") || RADIOTYPE.equalsIgnoreCase("nr")) {
                    int mcc = cellData.getM_mobileCountryCode();
                    if (mcc > 9 & mcc < 1000)
                        MCC = mcc;
                    else
                        continue;
                    int mnc = cellData.getM_mobileNetworkCode();
                    if (mnc > 9 & mnc < 1000)
                        MNC = mnc;
                    else
                        continue;
                    Log.d(RADIOTYPE + " = ", MCC + ":" + MNC);
                }
                break;
            }
        }
    }

    public void setGsmScannedCells(boolean isGsmPrim, final CellSignalStrengthGsm gsm, final CellIdentityGsm identityGsm) {
        JiotCustomCellData localCellData = new JiotCustomCellData();
        localCellData.setM_radioType("gsm");
        localCellData.setM_signalStrength(gsm.getDbm());
        localCellData.setM_isPrimary(isGsmPrim);
        localCellData.setM_cellId(identityGsm.getCid());
        localCellData.setM_psc(identityGsm.getPsc());
        localCellData.setM_mobileNetworkCode(identityGsm.getMnc());
        localCellData.setM_mobileCountryCode(identityGsm.getMcc());
        localCellData.setM_locationAreaCode(identityGsm.getLac());
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            localCellData.setM_timingAdvance(gsm.getTimingAdvance());
        } else {
            localCellData.setM_timingAdvance(-1);
        }
        Log.d("V2RTLS", "GSM MCC, MNC, cell id " + identityGsm.getMcc() + " " + identityGsm.getMnc() + " " + identityGsm.getCid());
        m_CustomCellDataAll.add(localCellData);
    }

    public void setCdmaScannedCells(boolean isCdmaPrim, final CellSignalStrengthCdma cdma, final CellIdentityCdma identityCdma) {
        JiotCustomCellData localCellData = new JiotCustomCellData();
        localCellData.setM_cellId(identityCdma.getBasestationId());
        localCellData.setM_mobileNetworkCode(identityCdma.getNetworkId());
        localCellData.setM_mobileCountryCode(MCC);
        localCellData.setM_psc(identityCdma.getSystemId());
        localCellData.setM_locationAreaCode(LAC);
        localCellData.setM_signalStrength(cdma.getDbm());
        localCellData.setM_isPrimary(isCdmaPrim);
        localCellData.setM_radioType("cdma");
        localCellData.setM_latitude(identityCdma.getLatitude());
        localCellData.setM_longitude(identityCdma.getLongitude());
        localCellData.setM_timingAdvance(-1); //CDMA does not contain Timing advance value
        Log.d("V2RTLS", "CDMA MCC, MNC, cell id " + MCC + " " + MNC + " " + identityCdma.getBasestationId());
        m_CustomCellDataAll.add(localCellData);
    }

    public void setLteScannedCells(boolean isLtePrim, final CellSignalStrengthLte lte, final CellIdentityLte identityLte) {
        JiotCustomCellData localCellData = new JiotCustomCellData();
        boolean salt = false;

        if (salt && !isLtePrim) {
            return;
        }
        if (salt) {
            localCellData.setM_cellId(identityLte.getCi());
            localCellData.setM_mobileCountryCode(identityLte.getMcc());
            localCellData.setM_mobileNetworkCode(identityLte.getMnc());
            localCellData.setM_locationAreaCode(identityLte.getTac());
            localCellData.setM_isPrimary(isLtePrim);
            localCellData.setM_radioType("lte");
        } else {
            localCellData.setM_isPrimary(isLtePrim);
            localCellData.setM_radioType("lte");
            localCellData.setM_signalStrength(lte.getDbm());
            localCellData.setM_cellId(identityLte.getCi());
            localCellData.setM_psc(identityLte.getPci());
            localCellData.setM_mobileCountryCode(identityLte.getMcc());
            localCellData.setM_mobileNetworkCode(identityLte.getMnc());
            localCellData.setM_locationAreaCode(identityLte.getTac());
            localCellData.setM_timingAdvance(lte.getTimingAdvance());
            localCellData.setM_frequency(identityLte.getEarfcn());
        }
        Log.d("V2RTLS", "LTE MCC, MNC, cell id " + identityLte.getMcc() + " " + identityLte.getMnc() + " " + identityLte.getCi());
        m_CustomCellDataAll.add(localCellData);
    }

    public void setWcdmaScannedCells(boolean isWcdmaPrim, final CellSignalStrengthWcdma wcdma, final CellIdentityWcdma identityWcdma) {
        JiotCustomCellData localCellData = new JiotCustomCellData();
        localCellData.setM_cellId(identityWcdma.getCid());
        localCellData.setM_locationAreaCode(identityWcdma.getLac());
        localCellData.setM_radioType("wcdma");
        localCellData.setM_mobileNetworkCode(identityWcdma.getMnc());
        localCellData.setM_mobileCountryCode(identityWcdma.getMcc());
        localCellData.setM_psc(identityWcdma.getPsc());
        localCellData.setM_isPrimary(isWcdmaPrim);
        localCellData.setM_signalStrength(wcdma.getDbm());
        localCellData.setM_timingAdvance(-1);
        m_CustomCellDataAll.add(localCellData);
    }

    public void setNrScannedCells(boolean isNrPrimary, final CellSignalStrengthNr nrSignal, final CellIdentityNr nrIdentity) {
        JiotCustomCellData localCellData = new JiotCustomCellData();
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            localCellData.setM_cellId(nrIdentity.getNci());
            localCellData.setM_isPrimary(isNrPrimary);
            localCellData.setM_locationAreaCode(nrIdentity.getTac());
            localCellData.setM_radioType("nr");
            localCellData.setM_mobileCountryCode(Integer.parseInt(nrIdentity.getMccString()));
            localCellData.setM_mobileNetworkCode(Integer.parseInt(nrIdentity.getMncString()));
            localCellData.setM_psc(nrIdentity.getPci());
            localCellData.setM_signalStrength(nrSignal.getDbm());
            localCellData.setM_timingAdvance(-1);
            m_CustomCellDataAll.add(localCellData);
        }
    }


    public void getWifiInfo() {
        Log.d("WIFI", "getWifiInfo");
        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> apList = wifiManager.getScanResults();
        JiotRtlsWifiData selfWifi = null;
//        if (wifiManager.isWifiEnabled()) {
//            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//            if (wifiInfo.getNetworkId() != -1) {
//                selfWifi = new JiotRtlsWifiData();
//                selfWifi.setM_BSSID(wifiInfo.getBSSID());
//                selfWifi.setM_frequency(wifiInfo.getFrequency());
//                selfWifi.setM_SSID(wifiInfo.getSSID());
//                selfWifi.setM_level(wifiInfo.getRssi());
//                mSelfWifi = selfWifi;
//            }
//        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Shared_prefs", Context.MODE_PRIVATE);
        String bssid = sharedPreferences.getString("bss", null);
        String ssid = sharedPreferences.getString("ss", null);
        int rssi = sharedPreferences.getInt("rs", 0);
        if (bssid != null && rssi != 0) {
            selfWifi = new JiotRtlsWifiData();
            selfWifi.setM_BSSID(bssid);
            selfWifi.setM_SSID(ssid);
            selfWifi.setM_level(rssi);
            selfWifi.setM_frequency(0);
            mSelfWifi = selfWifi;
        }

        for (ScanResult wifiItem : apList) {
            if (null != selfWifi && selfWifi.getM_BSSID().equalsIgnoreCase(wifiItem.BSSID)) {
                continue;
            }
            JiotRtlsWifiData m_jiotRtlsWifiData = new JiotRtlsWifiData();
            Log.d("WIFIMAC", wifiItem.BSSID);
            m_jiotRtlsWifiData.setM_BSSID(wifiItem.BSSID);
            Log.d("WIFISSID", wifiItem.SSID);
            m_jiotRtlsWifiData.setM_SSID(wifiItem.SSID);
            Log.d("WIFISIGNAL", wifiItem.level + "");
            m_jiotRtlsWifiData.setM_level(wifiItem.level);
            Log.d("WIFIFREQ", wifiItem.frequency + "");
            m_jiotRtlsWifiData.setM_frequency(wifiItem.frequency);
            Log.d("WIFIFCHAN", wifiItem.channelWidth + "");
            m_jiotRtlsWifiData.setM_channelWidth(wifiItem.channelWidth);
            Log.d("WIFIFOPER", wifiItem.operatorFriendlyName + "");
            m_jiotRtlsWifiDataList.add(m_jiotRtlsWifiData);
        }
    }

    public void getCellInfo() {
        Log.d("getCellInfo", "getCellInfo called");
        m_CustomCellDataAll.clear();
        TelephonyManager m_telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = m_telephonyManager.getNetworkOperator();
        getMccMnc(networkOperator);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        List<CellInfo> cellLocation = m_telephonyManager.getAllCellInfo();
        //List<NeighboringCellInfo> neighbours = m_telephonyManager.getNeighboringCellInfo();
        if (cellLocation != null) {
            Log.d("SIZECELL", cellLocation.size() + "");
            //  if (neighbours != null)
            //    Log.d("NEIGBOURS ", neighbours.size() + "");
            for (CellInfo info : cellLocation) {

                if (info instanceof CellInfoGsm) {
                    final CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                    final CellIdentityGsm identityGsm = ((CellInfoGsm) info).getCellIdentity();
                    boolean isGsmPrim = info.isRegistered();
                    if (isGsmPrim) {
                        JiotUtils.isGsmPrimary = true;
                    }
//                    String mcc = String.valueOf(identityGsm.getMcc());
                    Log.d("V2RTLS", "LTE MCC, MNC, cell id " + identityGsm.getMcc() + " " + identityGsm.getMnc());
//                    if (mcc != null && mcc.length() <= 3) {
                    setGsmScannedCells(isGsmPrim, gsm, identityGsm);
//                    }
                }
                if (info instanceof CellInfoLte) {
                    final CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                    final CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();
                    boolean isLtePrim = info.isRegistered();
                    if (isLtePrim) {
                        JiotUtils.isLtePrimary = true;
                    }
//                    String mcc = String.valueOf(identityLte.getMcc());
//                    if (mcc != null && mcc.length() <=3 ) {
                    setLteScannedCells(isLtePrim, lte, identityLte);
//                    }
                    Log.d("LTECELLID", CELLID + "");
                }
                if (info instanceof CellInfoCdma) {
                    final CellSignalStrengthCdma cdma = ((CellInfoCdma) info).getCellSignalStrength();
                    final CellIdentityCdma identityCdma = ((CellInfoCdma) info).getCellIdentity();
                    boolean isCdmaPrim = info.isRegistered();
                    if (isCdmaPrim) {
                        JiotUtils.isCdmaPrimary = true;
                    }
                    setCdmaScannedCells(isCdmaPrim, cdma, identityCdma);
                }
                if (info instanceof CellInfoWcdma) {
                    final CellSignalStrengthWcdma wcdmaSignal = ((CellInfoWcdma) info).getCellSignalStrength();
                    final CellIdentityWcdma wcdmaIdentity = ((CellInfoWcdma) info).getCellIdentity();
                    boolean isWcdmaPrim = info.isRegistered();
                    if (isWcdmaPrim) {
                        JiotUtils.isWcdmaPrimary = true;
                    }
//                    String mcc = String.valueOf(wcdmaIdentity.getMcc());
                    Log.d("V2RTLS", "LTE MCC, MNC, cell id " + wcdmaIdentity.getMcc() + " " + wcdmaIdentity.getMnc());
//                    if (mcc != null && mcc.length() <=3 ) {
                    setWcdmaScannedCells(isWcdmaPrim, wcdmaSignal, wcdmaIdentity);
//                    }
                }

                if (android.os.Build.VERSION.SDK_INT >= 29) {
                    if (info instanceof CellInfoNr) {
                        final CellSignalStrengthNr nrSignal = (CellSignalStrengthNr) ((CellInfoNr) info).getCellSignalStrength();
                        final CellIdentityNr nrIdentity = (CellIdentityNr) ((CellInfoNr) info).getCellIdentity();
                        boolean isNrPrim = info.isRegistered();
//                        String mcc = String.valueOf(nrIdentity.getMccString());
                        Log.d("V2RTLS", "LTE MCC, MNC, cell id " + nrIdentity.getMccString() + " " + nrIdentity.getMncString());
//                        if (mcc != null && mcc.length() <=3 ) {
                        setNrScannedCells(isNrPrim, nrSignal, nrIdentity);
//                        }

                    }
                }

            }
            setPrimaryCellIds();
        } else {
            Log.d("NOCELL", "NO CELLS FOUND");
        }
    }

    private void makeAPICall(String url) {
        final JSONObject jsonMainBody = createV2JsonObject();
        Log.d("JSONRTLSV3", jsonMainBody.toString());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        showProgressWaitDialog();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonMainBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dimissProgressWaitDialog();
                try {
                    mRtlsFetchTimeEndInMs = Calendar.getInstance().getTimeInMillis();
                    Log.d("MSGFROMSERVERV3", "Location Fetch SUCCESS: " + response);
                    String message = response.optString("msg");
                    if (message != null && !message.isEmpty()) {
                        Log.d("Error ", message);
                        return;
                    }
                    sendAllLocationsToMaps(response);
                } catch (Exception e) {
                    Log.d("EXCEPTION", "exce");
                    e.printStackTrace();
                }
            }
        }, error -> {
            String errorMsg = JiotUtils.getVolleyError(error);
            dimissProgressWaitDialog();
            if (error.networkResponse != null && error.networkResponse.statusCode == 401
                    && !JiotUtils.isTokenExpired) {
                JiotUtils.isTokenExpired = true;
                sendRefreshToken();
            }
            GetLocationAPIResponse getLocationAPIResponse = JiotUtils.getInstance().getPojoObject(errorMsg, GetLocationAPIResponse.class);
            if (getLocationAPIResponse.getError() != null && getLocationAPIResponse.getError().getCode() == 404
                    && getLocationAPIResponse.getError().getMessage().equalsIgnoreCase("Location Not Found")) {
                mDbManager.insertCellInfoInDB(jsonMainBody);
            }
            JiotUtils.showErrorToast(getActivity(), errorMsg);
            Log.e("MSGFROMSERVER", "FAILURE " + errorMsg);
            if (error != null) {
//                    m_jiotSdkFileLoggerInstance.JiotWriteLogDataToFile("RTLSFAILRESPONSE" + " $ " + errorMsg);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("token", JiotUtils.jiotgetRtlsToken(getActivity()));
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                String number = sharedPreferences.getString("mob", null);
                if (number != null) {
                    headers.put("msisdn", number);
                }
                return headers;
            }
        };
        mRtlsFetchTimeStartInMs = Calendar.getInstance().getTimeInMillis();
        queue.add(req);
    }

    public JSONObject createV2JsonObject() {
        JSONObject jsonCellsBody = new JSONObject();
        boolean salt = false;
        try {
            JSONArray jsonGsmArray = new JSONArray();
            JSONArray jsonLteArray = new JSONArray();
            JSONArray jsonWcdmaArray = new JSONArray();
            JSONObject gpsCdma = new JSONObject();
            boolean cdmaGps = false;
            for (JiotCustomCellData cellData : m_CustomCellDataAll) {
                if (cellData.getM_cellId() != 0) {
                    String s = String.valueOf(cellData.getM_mobileCountryCode());
                    if (null != s && s.length() > 3) {
                        Log.d("V2RTLS", "MCC greater than 3 digits " + s);
                        continue;
                    }

                    JSONObject jsonPlainCellTowerData = new JSONObject();
                    int mcc = cellData.getM_mobileCountryCode();
                    if (mcc > 9 & mcc < 1000)
                        jsonPlainCellTowerData.put("mcc", mcc);
                    else
                        continue;
                    // MNC
                    int mnc = cellData.getM_mobileNetworkCode();
                    if (mnc > 9 & mnc < 1000)
                        jsonPlainCellTowerData.put("mnc", mnc);
                    else
                        continue;
                    //TAC
                    if (cellData.getM_radioType().equalsIgnoreCase("lte") || (!salt)) {
                        int tac = cellData.getM_locationAreaCode();
                        TAC = tac;
                        if (tac >= 0 && tac <= 65536) {
                            jsonPlainCellTowerData.put("tac", tac);
                        } else {
                            continue;
                        }
                    }
                    // CellID
                    Long cellId = cellData.getM_cellId();
                    if (cellId > 0)
                        jsonPlainCellTowerData.put("cellId", cellId);
                    else
                        continue;
                    if (!salt) {
                        int rssi = cellData.getM_signalStrength();
                        if (rssi >= -150 && rssi <= 0)
                            jsonPlainCellTowerData.put("rssi", cellData.getM_signalStrength());
                        else
                            continue;
                        if (cellData.getM_radioType().equalsIgnoreCase("lte")) {
                            int frequency = cellData.getFrequency();
                            FREQUENCY = frequency;
                            if (frequency >= 1 && frequency <= 99999999)
                                jsonPlainCellTowerData.put("frequency", frequency);
                            else
                                continue;
                        }
                    }
                    if (cellData.getM_radioType().equalsIgnoreCase("gsm")) {
                        jsonGsmArray.put(jsonPlainCellTowerData);
                    } else if (cellData.getM_radioType().equalsIgnoreCase("wcdma")) {
                        jsonWcdmaArray.put(jsonPlainCellTowerData);
                    } else if (cellData.getM_radioType().equalsIgnoreCase("lte")) {
                        jsonLteArray.put(jsonPlainCellTowerData);
                    }

                    if (cellData.getM_radioType().equalsIgnoreCase("cdma") && (cdmaGps == false)) {
                        gpsCdma.put("lat", cellData.getM_latitude());
                        gpsCdma.put("lon", cellData.getM_longitude());
                        cdmaGps = true;
                        Log.d("GPS", "ADDED CDMA GPS COORD");
                    }
                }
            }

            if (cdmaGps == true) {
                jsonCellsBody.put("gps", gpsCdma);
            } else {
                JSONObject testLocation = new JSONObject();
                testLocation.put("lat", JiotUtils.sLang);
                testLocation.put("lng", JiotUtils.slon);
                jsonCellsBody.put("gps", testLocation);
            }
            jsonCellsBody.put("ltecells", jsonLteArray);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("V2RTLS", jsonCellsBody.toString());
        return jsonCellsBody;
    }

    public void sendAllLocationsToMaps(JSONObject response) {
        try {
            Double lat = response.getDouble("lat");
            Double lng = response.getDouble("lng");
            Log.i("Received Location", lat + ", " + lng);
            LatLng newLatLng = new LatLng(lat, lng);
            m_servIdLocation.put("test-104", newLatLng);
            Intent intent = new Intent();
            intent.setAction("com.rtls.location_all");
            intent.putExtra("CELLID", CELLID);
            intent.putExtra("ALLPINS", m_servIdLocation);
            getActivity().sendBroadcast(intent);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void sendRefreshToken() {
        Intent intent = new Intent();
        intent.setAction("com.rtls.token_error");
        getActivity().sendBroadcast(intent);
    }

}