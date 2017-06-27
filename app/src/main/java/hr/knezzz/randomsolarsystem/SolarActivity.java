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
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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

import hr.knezzz.randomsolarsystem.utils.Resources;
import hr.knezzz.randomsolarsystem.utils.Utils;

public class SolarActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int QR_CODE_REQUEST = 1;
    DrawerLayout drawer;
    FloatingActionButton fab;

    private static final String TAG = "SolarActivity";
    private ArrayList<Planet> planets;
    private long supercluster, cluster, galaxy;
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
    private DecimalFormat format = new DecimalFormat("###,###");
    private Bitmap qrCode = null;

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

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
//        LeakCanary.install(getApplication());
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

        if(SolarSingleton.getInstance().getSolarSeed() != null){
            seedToString = SolarSingleton.getInstance().getSolarSeed();
            startSolarSystem(false);
        }else{
            startSolarSystem(true);
        }

        adapter = new PlanetAdapter(this, this, R.layout.solarsystem_header, planets, sun, canvas);
        layoutManager = new LinearLayoutManager(this);
        solarSystemRecyclerView.setLayoutManager(layoutManager);
        solarSystemRecyclerView.setAdapter(adapter);
    }

    /**
     * Change seed
     * @param generateNewSystem - generate random system (true) : generate from current seed (false)
     */
    private void startSolarSystem(boolean generateNewSystem){
        qrCode = null;

        if(generateNewSystem)
            getNewSolarSystem();//this.seed = BigInteger.valueOf(new Random().nextLong());

        getSun();
        getSolarSystemPosition(seedToString);
        getPlanets(seedToString);

        toolbarTitle.setText(String.format("%s", seedToString.replace(":", "")));
        setToolbarColors();
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
        supercluster = _superCluster;//position.nextInt(131072) + 32768;//solarSeed.nextInt(131072) + 32768;
        cluster = _cluster;//position.nextInt(900) + 100;//solarSeed.nextInt(900)+100;
        star = _star;//(long) (new Random(_star).nextDouble()*515396075520L + 34359738368L);//new BigInteger("34359738368").add(BigInteger.valueOf((long) (solarSeed.nextFloat()*515396075520L)));

        setNavItemCount(R.id.galaxy, galaxy);
        setNavItemCount(R.id.cluster, cluster);
        setNavItemCount(R.id.superCluster, supercluster);
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
        supercluster = solarSeed.nextInt(131072 + 32768);//solarSeed.nextInt(131072) + 32768;
        cluster = solarSeed.nextInt(50000);//solarSeed.nextInt(900)+100;
        star = 1L;//((long)position.nextInt(255) * -1L);//It goes 256 times through loop so if all 256 times are 1 position will be 256-255 = 1; 549755813888
        for(int i = 0; i < 256; i++){
            star += solarSeed.nextInt(Integer.MAX_VALUE);
        }
        star += solarSeed.nextInt(255);

        locationToStringSeed(partOfUniverse, supercluster, cluster, galaxy, star);
    }

    /**
     * Get new solar system but without changing current seed
     * @return - string seed of new system
     */
    private String getNewSolarSystemType(){
        Random position = new Random();
        partOfUniverse = position.nextInt(100);//solarSeed.nextLong();
        galaxy = position.nextInt(1000);///solarSeed.nextInt(49000)+1000;
        supercluster = position.nextInt(131072 + 32768);//solarSeed.nextInt(131072) + 32768;
        cluster = position.nextInt(50000);//solarSeed.nextInt(900)+100;
        star = 1L;////It goes 256 times through loop so if all 256 times are 0 position will be 0+1 = 1;

        for(int i = 0; i < 256; i++){
            star += position.nextInt(Integer.MAX_VALUE);
        }
        star += position.nextInt(255);

        return partOfUniverse + ":" + supercluster + ":" + cluster + ":" + galaxy  + ":" +star;//BigInteger.valueOf(new Random().nextLong());
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
     * Change titlebar color
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

    /**
     * Change seed
     * @param generateNewSystem - generate random system (true) : generate from current seed (false)
     */
    private void updateSolarSystem(boolean generateNewSystem){
        qrCode = null;

        if(generateNewSystem)
            getNewSolarSystem();//this.seed = BigInteger.valueOf(new Random().nextLong());

        getSolarSystemPosition(seedToString);
        getPlanets(seedToString);

        SolarSingleton.getInstance().setInstance(seedToString);
        adapter.changeSolarSystem(planets, getSun());

        toolbarTitle.setText(String.format("%s", seedToString.replace(":", "")));
        setToolbarColors();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        setNavItemCount(R.id.superCluster, supercluster);
        setNavItemCount(R.id.cluster, cluster);
        setNavItemCount(R.id.galaxy, galaxy);
        setNavItemCount(R.id.star, star);

        return true;
    }

    FindSeed searchThread;

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
            case R.id.get_qr_code:
                getSeedAsQRCode();
                return super.onOptionsItemSelected(item);
            case R.id.scan_qr_code:
                startActivityForResult(new Intent(this, QRScanner.class), QR_CODE_REQUEST);
                return super.onOptionsItemSelected(item);
            default:
                type = Resources.STAR_TYPE_K;
        }

        searchThread = new FindSeed(type);
        //ExecutorService pool = Executors.newCachedThreadPool();
      //  pool.submit(new FindSeed(type));
        searchThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.part_of_universe) {
            changeSeedDialog("Part of the Universe", 100, partOfUniverse, 0);
        } else if (id == R.id.superCluster) {
            changeSeedDialog("Super Cluster", 131072 + 32768, supercluster, 1);
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
    private void changeSeedDialog(String title, long limit, long current, final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        builder.setMessage("limit ["+format.format(limit)+"]");
        builder.setCancelable(true);

        final EditText input = new EditText(this);
        input.setHint(format.format(current));

        input.requestFocus();
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.RESULT_SHOWN, 0);

        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                imm.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0);
            }
        });

        builder.setPositiveButton("GO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String myseed = input.getText().toString();

                if(myseed.equalsIgnoreCase("")) {
                    return;
                }

                String[] seedasstring = seedToString.split(":");

                seedasstring[position] = myseed;
                locationToStringSeed(Long.parseLong(seedasstring[0]),Long.parseLong(seedasstring[1]),Long.parseLong(seedasstring[2]),Long.parseLong(seedasstring[3]),Long.parseLong(seedasstring[4]));
                updateSolarSystem(false);
            }
        });

        builder.setView(input);
        builder.show();
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
        int solarSystemSize = 4 + planetRandom.nextInt(11);
        //TODO: Change.. find better way to get distance number.
        long freeSpaceForPlanets = 70;//((Resources.BIGGEST_SUN_DISTANCE - Resources.SHORTEST_SUN_DISTANCE) + Resources.SHORTEST_SUN_DISTANCE) / solarSystemSize;

        for(int i = 1; i <= solarSystemSize; i++){
            String planetSeed = seed +":"+ i;
            long distance;
            if(i == 1) {
                distance = (long) (planetRandom.nextDouble() * ((freeSpaceForPlanets/2) * i)) + freeSpaceForPlanets/2;
            }else{
                distance = (long) ((planetRandom.nextDouble() * (freeSpaceForPlanets - lastPlanet.getModelSize(true))) + (freeSpaceForPlanets*i)) + lastPlanet.getModelSize(true);
            }
            Planet ssPlanet = new Planet(stringToSeed(planetSeed));

            ssPlanet.setDistanceFromSun(distance);
            lastPlanet = ssPlanet;
            Log.i("Creating planet", "Goldilocks ["+ (ssPlanet.getGoldilocksZone()?1:0) + "] Simple life [" + (ssPlanet.getSimpleLife()?1:0) + "] Complex life [" + (ssPlanet.getComplexLife()?1:0) + "] Seed ["+i+"] Name [" + ssPlanet.getName() + "]");
            planets.add(ssPlanet);
        }

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
                    try {
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
                    } catch (WriterException e) {
                        e.printStackTrace();
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

        final Dialog alertadd = new Dialog(this);
        alertadd.setCancelable(false);
        alertadd.addContentView(qrDialog, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        alertadd.show();

        return alertadd;
    }

    private Bitmap encodeAsBitmap(String str) throws WriterException {
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
     * @param supercluster - super cluster
     * @param cluster - cluster
     * @param galaxy - galaxy
     * @param star - star
     * @return -  return seed based on location;
     */
    private String locationToStringSeed(long part, long supercluster, long cluster, long galaxy, long star){
        seedToString = part + ":" + supercluster + ":" + cluster + ":" + galaxy  + ":" +star;
        return seedToString;
    }

    /**
     * Transform string to seed
     * @param seed - string seed
     * @return - BigInteger seed
     */
    private BigInteger stringToSeed(String seed){
        return new BigInteger(seed.replace(":",""));
    }

    boolean showModel = false;

    /**
     * Toggle view between model and listview
     */
    private void toggleView(){
        if(!showModel){
            layoutManager.scrollToPosition(0);//solarSystemRecyclerView, null, 0);
            //   solarSystemView.setVisibility(View.GONE);
            //         sunInfo.setVisibility(View.GONE);
            menuActionToggle.setText("SHOW\nLIST");
            fab.hide();
            //   solarSystemView.setPadding(0, sunSizeModel+100, 0, 0);
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
            //          sunInfo.setVisibility(View.VISIBLE);
            layoutManager.smoothScrollToPosition(solarSystemRecyclerView, null, 0);
            menuActionToggle.setText("SHOW\nMODEL");
            adapter.toggleViews();
            //   solarSystemView.setVisibility(View.VISIBLE);

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
            }, 1000);
        }

        showModel = !showModel;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == QR_CODE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                seedToString = data.getStringExtra("SEED");
                Log.e(TAG, "Got :"+seedToString);

                String[] location = seedToString.split(":");

                if(location.length > 5) {
                    seedToString = seedToString.substring(0, seedToString.lastIndexOf(":"));
                    Log.e(TAG, "Changed to :"+seedToString);
                    updateSolarSystem(false);

                    final int _planet = Integer.parseInt(location[5]);
                    if(planets.size() >= _planet-1) {
                        Planet currentPlanet = planets.get(_planet-1);
                        Intent i = new Intent(this, PlanetActivity.class);
                        i.putExtra(Resources.PLANET_INTENT_WHOLE_PLANET, currentPlanet);
                        startActivity(i);
                    }
                }else {
                    updateSolarSystem(false);
                }
                // Do something with the contact here (bigger example below)
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
    *                   made it so it uses all avaible cores for the search.
    *
    *
    ************************************************************************************************
    ************************************************************************************************
    */
    class FindSeed extends AsyncTask<Void, String, Integer> {
        boolean searching = false;
        boolean foundStar = false;
        int type;
        private ProgressDialog progressDialog = null;

        public FindSeed(int type){
            this.type = type;
        }

        public boolean isSearching(){
            return searching;
        }

        @Override
        protected void onPostExecute(Integer integer) {
          //  super.onPostExecute(integer);

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

                if(loopCount%82 == 0){
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
