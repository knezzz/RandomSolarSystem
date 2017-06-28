package hr.knezzz.randomsolarsystem;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import hr.knezzz.randomsolarsystem.utils.PlanetView;
import hr.knezzz.randomsolarsystem.utils.Resources;
import hr.knezzz.randomsolarsystem.utils.Utils;

public class SolarActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final int QR_CODE_REQUEST = 1;
    private DrawerLayout drawer;
    private FloatingActionButton fab;

    private static final String TAG = "SolarActivity";
    private ArrayList<PlanetView> planets;
    private long superCluster, cluster, galaxy;
    private long partOfUniverse;
    private long star;
    private Sun sun;
    private Random solarSeed;
    private String seedToString = null;
  //  private BigInteger seed;
    private TextView menuActionToggle;
    private RecyclerView solarSystemRecyclerView;
    private PlanetAdapter adapter;
    private RelativeLayout canvas;
    private LinearLayoutManager layoutManager;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private NavigationView navigationView;
    private final DecimalFormat format = new DecimalFormat("###,###");
    private Bitmap qrCode = null;

    private FindSeed searchThread;

    private boolean showModel = false;

    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Resources.DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSolarSystem(true);
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        solarSystemRecyclerView = (RecyclerView) findViewById(R.id.solarSystemRecyclerView);

        menuActionToggle = (TextView) toolbar.findViewById(R.id.toggle_view_button);
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);

        canvas = (RelativeLayout) findViewById(R.id.solar_system_canvas);

        menuActionToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleView();
            }
        });

        adapter = new PlanetAdapter(this, this, planets, sun, canvas);
        layoutManager = new LinearLayoutManager(this);
        solarSystemRecyclerView.setLayoutManager(layoutManager);
        solarSystemRecyclerView.setAdapter(adapter);

        if(SolarSingleton.getInstance().getSolarSeed() != null){
            seedToString = SolarSingleton.getInstance().getSolarSeed();
            updateSolarSystem(false);
        }else{
            updateSolarSystem(true);
        }
    }

    /**
     * Change seed
     * @param generateNewSystem - generate random system (true) : generate from current seed (false)
     */
    private void updateSolarSystem(boolean generateNewSystem){
        qrCode = null;

        if(generateNewSystem)
            getNewSolarSystem();//this.seed = BigInteger.valueOf(new Random().nextLong());

        Sun _sun = getSun();
        getSolarSystemPosition(seedToString);
        getPlanets(seedToString);

        SolarSingleton.getInstance().setInstance(seedToString);
        adapter.changeSolarSystem(planets, _sun);

        toolbarTitle.setText(String.format("%s", seedToString.replace(":", "")));
        setToolbarColors();
    }

    private ArrayList<PlanetView> getPlanetViews(ArrayList<Planet> planets){
        ArrayList<PlanetView> views = new ArrayList<>();
        if(planets == null || planets.isEmpty()){
            return views;
        }

        for(Planet p : planets){
            views.add(new PlanetView(p, this));
        }

        return views;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(seedToString == null) {
            if (SolarSingleton.getInstance().getSolarSeed() != null) {
                seedToString = SolarSingleton.getInstance().getSolarSeed();
                updateSolarSystem(false);
            }
        }
    }

    /**
     * Get position of current solar system
     */
    private void getSolarSystemPosition(String seed) {
        seedToString = seed;
        Log.d(TAG, seed);
        String[] location = seed.split(":");

        long _partOfUniverse = Long.parseLong(location[0]);
        long _superCluster = Long.parseLong(location[1]);
        long _cluster = Long.parseLong(location[2]);
        long _galaxy = Long.parseLong(location[3]);
        long _star = Long.parseLong(location[4]);


        partOfUniverse = _partOfUniverse;//position.nextInt(100);//solarSeed.nextLong();
        galaxy = _galaxy;//position.nextInt(49000) + 1000;///solarSeed.nextInt(49000)+1000;
        superCluster = _superCluster;//position.nextInt(131072) + 32768;//solarSeed.nextInt(131072) + 32768;
        cluster = _cluster;//position.nextInt(900) + 100;//solarSeed.nextInt(900)+100;
        star = _star;//(long) (new Random(_star).nextDouble()*515396075520L + 34359738368L);//new BigInteger("34359738368").add(BigInteger.valueOf((long) (solarSeed.nextFloat()*515396075520L)));

        setNavItemCount(R.id.galaxy, galaxy);
        setNavItemCount(R.id.cluster, cluster);
        setNavItemCount(R.id.superCluster, superCluster);
        setNavItemCount(R.id.star, star);
        setNavItemCount(R.id.part_of_universe, partOfUniverse);
    }

    /**
     * Get new solar system and show it immediately.
     */
    private void getNewSolarSystem(){
        solarSeed = new Random();
        partOfUniverse = solarSeed.nextInt(100);//solarSeed.nextLong();
        galaxy = solarSeed.nextInt(1000);///solarSeed.nextInt(49000)+1000;
        superCluster = solarSeed.nextInt(131072 + 32768);//solarSeed.nextInt(131072) + 32768;
        cluster = solarSeed.nextInt(50000);//solarSeed.nextInt(900)+100;
        star = (long)(solarSeed.nextDouble() * 549755813888L);//549755813888
        locationToStringSeed(partOfUniverse, superCluster, cluster, galaxy, star);
    }

    /**
     * Get new solar system but without changing current seed
     * @return - string seed of new system
     */
    private String getNewSolarSystemType(){
        Random position = new Random();
        partOfUniverse = position.nextInt(100);//solarSeed.nextLong();
        galaxy = position.nextInt(1000);///solarSeed.nextInt(49000)+1000;
        superCluster = position.nextInt(131072 + 32768);//solarSeed.nextInt(131072) + 32768;
        cluster = position.nextInt(50000);//solarSeed.nextInt(900)+100;
        star = (long)(position.nextDouble() * 549755813888L);//549755813888 stars
        return partOfUniverse + ":" + superCluster + ":" + cluster + ":" + galaxy  + ":" +star;//BigInteger.valueOf(new Random().nextLong());
    }

    /**
     * Setting count number in drawer
     * @param itemId - where is the item
     * @param count - message to be displayed
     */
    private void setNavItemCount(@IdRes int itemId, String count) {
        TextView view = (TextView) navigationView.getMenu().findItem(itemId).getActionView();
        view.setText(count);
    }

    /**
     * Setting count number in drawer
     * @param itemId - where is the item
     * @param count - message to be displayed
     */
    private void setNavItemCount(@IdRes int itemId, long count){
        setNavItemCount(itemId, String.format("%s", format.format(count)));
    }

    /**
     * Change titleBar color
     */
    private void setToolbarColors(){
        toolbar.setBackgroundColor(sun.getSunColor());
        fab.setBackgroundTintList(ColorStateList.valueOf(sun.getSunColor()));

        if(Utils.isLollipop()) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //Make color darker for status bar.
            float[] hsv = new float[3];
            Color.colorToHSV(sun.getSunColor(), hsv);
            hsv[2] *= 0.75f;

            window.setStatusBarColor(Color.HSVToColor(hsv));
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.solar, menu);

        setNavItemCount(R.id.part_of_universe, partOfUniverse);
        setNavItemCount(R.id.superCluster, superCluster);
        setNavItemCount(R.id.cluster, cluster);
        setNavItemCount(R.id.galaxy, galaxy);
        setNavItemCount(R.id.star, star);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();
        final int type;
        switch (id) {
            case R.id.find_O:
                type = Resources.STAR_TYPE_O;
                break;
            case R.id.find_B:
                type = Resources.STAR_TYPE_B;
                break;
            case R.id.find_A:
                type = Resources.STAR_TYPE_A;
                break;
            case R.id.find_F:
                type = Resources.STAR_TYPE_F;
                break;
            case R.id.find_G:
                type = Resources.STAR_TYPE_G;
                break;
            case R.id.find_K:
                type = Resources.STAR_TYPE_K;
                break;
            case R.id.get_qr_code:
                getSeedAsQRCode();
                return super.onOptionsItemSelected(item);
            case R.id.scan_qr_code:
                startActivityForResult(new Intent(this, QRScanner.class), QR_CODE_REQUEST);
                return super.onOptionsItemSelected(item);
            default:
                type = Resources.STAR_TYPE_M;
        }

        searchThread = new FindSeed(type);
        searchThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.part_of_universe) {
            changeSeedDialog("Part of the Universe", 100, partOfUniverse, 0);
        } else if (id == R.id.superCluster) {
            changeSeedDialog("Super Cluster", 131072 + 32768, superCluster, 1);
        } else if (id == R.id.cluster) {
            changeSeedDialog("Cluster", 50000, cluster, 2);
        } else if (id == R.id.galaxy) {
            changeSeedDialog("Galaxy", 1000, galaxy, 3);
        } else if (id == R.id.star) {
            changeSeedDialog("Star", 515396075520L + 34359738368L, star, 4);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Dialog that is shown when changing value from drawer.
     *
     * @param title - title to show.
     * @param limit - limit of int
     * @param current - current value to show as hint
     * @param position - position in drawer
     */
    private void changeSeedDialog(String title, final long limit, final long current, final int position){
        final Dialog seedDialog = new Dialog(this);
        seedDialog.setContentView(R.layout.dialog_change_seed);

        TextView titleTextView = (TextView) seedDialog.findViewById(R.id.dialog_title);
        TextView subtitleTextView = (TextView) seedDialog.findViewById(R.id.dialog_subtitle);
        final TextView infoTextView = (TextView) seedDialog.findViewById(R.id.dialog_info);
        final EditText seedEditText = (EditText) seedDialog.findViewById(R.id.dialog_edit_text);
        final TextInputLayout til = (TextInputLayout) seedDialog.findViewById(R.id.dialog_edit_text_hint);
        Button positiveButton = (Button) seedDialog.findViewById(R.id.dialog_button);

        titleTextView.setText(title);
        subtitleTextView.setText(String.format("Limit [%s]", format.format(limit)));

        seedEditText.requestFocus();
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.RESULT_SHOWN, 0);

        seedEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        seedDialog.setOnDismissListener(new DialogInterface.OnDismissListener(){
            @Override
            public void onDismiss(DialogInterface dialogInterface){
                imm.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0);
            }
        });

        updateDialogInfo(current + "", infoTextView, position);

        seedEditText.setHint(current + "");

        seedEditText.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){
                if(seedEditText.getText().toString().isEmpty()){
                }else if(Long.parseLong(seedEditText.getText().toString()) <= limit){
                    updateDialogInfo(seedEditText.getText().toString(), infoTextView, position);
                }else if(Long.parseLong(seedEditText.getText().toString()) > limit){
                    seedEditText.setText(String.format("%d", limit));
                }else if(Long.parseLong(seedEditText.getText().toString()) < 0){
                    seedEditText.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable){
                if(editable.length() == 0){
                    til.setHintEnabled(false);
                    seedEditText.setHint(current + "");
                }else{
                    til.setHintEnabled(true);
                    til.setHint(current + "");
                }
            }
        });


        positiveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String _seed = seedEditText.getText().toString();
                if(_seed.equalsIgnoreCase("")) {
                    return;
                }
                String[] seedToStringArray = seedToString.split(":");

                seedToStringArray[position] = _seed;
                locationToStringSeed(Long.parseLong(seedToStringArray[0]),Long.parseLong(seedToStringArray[1]),Long.parseLong(seedToStringArray[2]),Long.parseLong(seedToStringArray[3]),Long.parseLong(seedToStringArray[4]));
                updateSolarSystem(false);
                seedDialog.dismiss();
            }
        });

        seedDialog.show();
    }

    private void updateDialogInfo(String seed, TextView view, int position){
        if(seed.equalsIgnoreCase("")) {
            return;
        }
        String[] seedToStringArray = seedToString.split(":");

        seedToStringArray[position] = seed;
        view.setText(getSolarSystemInfo(Long.parseLong(seedToStringArray[0]),Long.parseLong(seedToStringArray[1]),Long.parseLong(seedToStringArray[2]),Long.parseLong(seedToStringArray[3]),Long.parseLong(seedToStringArray[4])));

    }

    private String getSolarSystemInfo(long l, long l1, long l2, long l3, long l4){
        String _seed = l + ":" + l1 + ":" + l2 + ":" + l3  + ":" +l4;
        Sun _sun = new Sun(stringToSeed(_seed));

        return _sun.toString();
    }

    /**
     * Get sun from seed
     * @return - get Sun
     */
    private Sun getSun(){
        sun = new Sun(stringToSeed(seedToString));
        Log.i("Creating sun", "Type ["+sun.getType()+"] Color ["+sun.getSunColor()+"] Size : [" + sun.getSunSizeInKm() + "] Mass : [" + sun.getSunMassInKg()+"]");
        return sun;
    }

    /**
     * Get planets from seed
     * @param seed - solar system seed
     */
    private void getPlanets(String seed){
        planets = new ArrayList<>();
        Planet lastPlanet = null;

        Random planetRandom = new Random(Long.parseLong(seed.split(":")[4]));
        int solarSystemSize = sun.getPlanetsCount();
        //TODO: Change.. find better way to get distance number.
        double freeSpaceForPlanets = ((double)(Resources.MODEL_BIGGEST_DISTANCE / solarSystemSize) / 2);//((Resources.BIGGEST_SUN_DISTANCE - Resources.SHORTEST_SUN_DISTANCE) + Resources.SHORTEST_SUN_DISTANCE) / solarSystemSize;
        double assignedSpaceForPlanets;
        int _sunSize = sun.getModelSize(true);
        double lastDistance = _sunSize;

        for(int i = 1; i <= solarSystemSize; i++){
            assignedSpaceForPlanets = freeSpaceForPlanets * i + lastDistance;

            String planetSeed = seed +":"+ i;
            long distance;

            if(i == 1) {
                distance = (long) (planetRandom.nextDouble() * assignedSpaceForPlanets) + _sunSize;
            }else{
                distance = (long) ((planetRandom.nextDouble() * (assignedSpaceForPlanets-lastDistance)) + lastDistance + lastPlanet.getModelSize(true) + (_sunSize/2));
            }

            lastDistance = assignedSpaceForPlanets;
            Planet ssPlanet = new Planet(stringToSeed(planetSeed), distance, sun.getIntType());
            lastPlanet = ssPlanet;
            Log.i("Creating planet", "Goldilocks ["+ (ssPlanet.getGoldilocksZone()?1:0) + "] Simple life [" + (ssPlanet.getSimpleLife()?1:0) + "] Complex life [" + (ssPlanet.getComplexLife()?1:0) + "] Seed ["+i+"] Name [" + ssPlanet.getName() + "]");
            PlanetView pv = new PlanetView(ssPlanet, this);
            pv.resetView(this);
            planets.add(pv);
        }

        adapter.changeSolarSystem(planets, sun);

    /*    Collections.sort(planets, new Comparator<Planet>() {
            @Override
            public int compare(Planet lhs, Planet rhs) {
                return Integer.valueOf(lhs.getPlanetSize()).compareTo(rhs.getPlanetSize());
            }
        });*/
    }

    private void getSeedAsQRCode(){
        if(qrCode != null){
            showImage(qrCode);
        }else {
            final Dialog searchDialog = showQRDialog();

            new Thread(new Runnable() {
                @Override
                public void run() {
                qrCode = encodeAsBitmap(seedToString);

                if (qrCode != null) {
                    final Bitmap finalBitmap = qrCode;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showImage(finalBitmap);
                            searchDialog.dismiss();
                        }
                    });
                }
                }
            }).start();
        }
    }

    private void showImage(Bitmap image){
        Dialog dialog = new Dialog(this);
        final View qrDialog= getLayoutInflater().inflate(R.layout.qr_dialog, canvas, false);
        ImageView imageView= (ImageView) qrDialog.findViewById(R.id.selectedImage);
        imageView.setImageBitmap(image);
        dialog.setCancelable(true);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//sunModel.getLayoutParams());
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

        imageView.setLayoutParams(params);
        dialog.addContentView(qrDialog, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.show();
    }

    private Dialog showQRDialog(){
        final View qrDialog= getLayoutInflater().inflate(R.layout.qr_dialog_searching, canvas, false);

        TextView solarSearchQRSeed = (TextView) qrDialog.findViewById(R.id.solar_seed_qr);
        solarSearchQRSeed.setText(seedToString);

        final Dialog showQRDialog = new Dialog(this);
        showQRDialog.setCancelable(false);
        showQRDialog.addContentView(qrDialog, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        showQRDialog.show();

        return showQRDialog;
    }

    private Bitmap encodeAsBitmap(String str){
        QRCodeWriter writer = new QRCodeWriter();
        int _size = Utils.getScreenHeight(SolarActivity.this)/3;
        try {
            BitMatrix bitMatrix = writer.encode(str, BarcodeFormat.QR_CODE, _size, _size);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.TRANSPARENT);
                }
            }

            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Custom location to seed.
     * @param part - part of universe
     * @param superCluster - super cluster
     * @param cluster - cluster
     * @param galaxy - galaxy
     * @param star - star
     */
    private void locationToStringSeed(long part, long superCluster, long cluster, long galaxy, long star){
        seedToString = part + ":" + superCluster + ":" + cluster + ":" + galaxy  + ":" +star;
    }

    /**
     * Transform string to seed
     * @param seed - string seed
     * @return - BigInteger seed
     */
    private BigInteger stringToSeed(String seed){
        return new BigInteger(seed.replace(":",""));
    }

    /**
     * Toggle view between model and listView
     */
    private void toggleView(){
        if(!showModel){
            layoutManager.scrollToPosition(0);
            menuActionToggle.setText("SHOW\nLIST");
            fab.hide();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.toggleViews();
                            solarSystemRecyclerView.setVisibility(View.GONE);
                        }
                    });
                }
            }, 100);
            solarSystemRecyclerView.setLayoutFrozen(true);
        }else{
            layoutManager.smoothScrollToPosition(solarSystemRecyclerView, null, 0);
            menuActionToggle.setText("SHOW\nMODEL");
            adapter.toggleViews();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            solarSystemRecyclerView.setVisibility(View.VISIBLE);
                            solarSystemRecyclerView.setLayoutFrozen(false);
                            fab.show();
                        }
                    });
                }
            }, 200);
        }

        showModel = !showModel;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QR_CODE_REQUEST) {
            if (resultCode == RESULT_OK) {
                seedToString = data.getStringExtra("SEED");
                Log.e(TAG, "Got :"+seedToString);

                String[] location = seedToString.split(":");

                if(location.length > 5) {
                    seedToString = seedToString.substring(0, seedToString.lastIndexOf(":"));
                    Log.e(TAG, "Changed to :"+seedToString);
                    updateSolarSystem(false);

                    final int _planet = Integer.parseInt(location[5]);
                    if(planets.size() >= _planet-1) {
                        PlanetView currentPlanet = planets.get(_planet-1);
                        Intent i = new Intent(this, PlanetActivity.class);
                        i.putExtra(Resources.PLANET_INTENT_WHOLE_PLANET, currentPlanet);
                        startActivity(i);
                    }
                }else {
                    updateSolarSystem(false);
                }
            }
        }
    }

    /*
    ************************************************************************************************
    ************************************************************************************************
    *
    *
    *
    *               Find seed class - extends AsyncTask for searching the new seed;
    *                   made it so it uses all available cores for the search.
    *
    *
    ************************************************************************************************
    ************************************************************************************************
    */
    class FindSeed extends AsyncTask<Void, String, Integer> {
        boolean searching = false;
        boolean foundStar = false;
        final int type;
        private ProgressDialog progressDialog = null;

        public FindSeed(int type){
            this.type = type;
        }

        public boolean isSearching(){
            return searching;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(foundStar){
                final Snackbar bar = Snackbar.make(solarSystemRecyclerView, "Found after " + format.format(integer) + " stars", Snackbar.LENGTH_LONG);
                bar.setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                bar.show();
                stop();
                updateSolarSystem(false);
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {

            searching = true;
            boolean isDialogShowing = false;
            int loopCount = 0;
            String _seedString = getNewSolarSystemType();
            long startTime = System.currentTimeMillis();

            while(searching) {
                if(!isDialogShowing && loopCount > 500){
                    isDialogShowing = true;
                    startSearchingDialog();
                    publishProgress(loopCount+"");
                }

                if(loopCount%142 == 0){
                    if(type == Resources.STAR_TYPE_O){
                        long _time = (System.currentTimeMillis()-startTime)/1000;
                        //450359962737049600000000000 <-- TODO:Total combinations
                        publishProgress(format.format(loopCount)+"", "Using threads: " +NUMBER_OF_CORES+ "\nTime passed: "+formatTime(_time)+"\nType O stars are extremely rare. About 3 in 10,000,000 so it will take a while to find one.\n\n" +
                                "50% at 1,666,667 \n100% at 3,333,334\n\n" +
                                format.format(13510798882L) +","+format.format(111488000000L) +" of them here...");
                    }else{
                        changeSearchingMessage(loopCount+"");
                    }
                }

                loopCount++;

                if (Sun.getSunType(stringToSeed(_seedString)) == type) {
                    searching = false;
                    foundStar = true;

                    final String final_seedString = _seedString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getSolarSystemPosition(final_seedString);
                        }
                    });
                }else {
                    _seedString = getNewSolarSystemType();
                }
            }

            return loopCount;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values.length > 1){
                changeSearchingMessage(values[0], values[1]);
            }else{
                changeSearchingMessage(values[0]);
            }
        }

        public void stop() {
            searching = false;
            closeSearchDialog();
            this.cancel(true);
        }

        /**
         * Start search dialog.
         */
        private void startSearchingDialog(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SolarActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog != null)
                                return;

                            progressDialog = new ProgressDialog(SolarActivity.this);
                            progressDialog.setTitle("Searching....");
                            progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    if(searchThread.isSearching()) {
                                        searchThread.stop();
                                        Log.d(TAG, "isSearching: "+searchThread.isSearching());
                                        searchThread = null;
                                    }
                                }
                            });
                            progressDialog.setIndeterminate(true);
                            progressDialog.show();
                        }
                    });
                }
            }).start();
        }

        private void changeSearchingMessage(String title){
            changeSearchingMessage(title, "");
        }

        /**
         * Change message - usually just time and search count.
         * @param title - Title of message
         * @param msg - message
         */
        private void changeSearchingMessage(final String title, final String msg){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SolarActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog == null)
                                return;

                            if (!progressDialog.isShowing())
                                return;

                            progressDialog.setTitle("Searching... " + title);
                            progressDialog.setMessage(msg);
                        }
                    });
                }
            }).start();
        }

        /**
         * Close search dialog
         */
        private void closeSearchDialog(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SolarActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(progressDialog == null)
                                return;

                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                    });

                }
            }).start();
        }

        /**
         * Format time from long to string
         *
         * @param time - long time
         * @return - return string time in format MM:SS
         */
        private String formatTime(long time){
            long min = time/60;
            long sec = time%60;
            String myTime = "";

            if(min == 0){
                myTime += "00";
            }else if(min < 10){
                myTime += "0" + min;
            }else{
                myTime += min;
            }

            myTime += ":";

            if(sec == 0){
                myTime += "00";
            }else if(sec < 10){
                myTime += "0" + sec;
            }else{
                myTime += sec;
            }

            return myTime;
        }
    }
}
