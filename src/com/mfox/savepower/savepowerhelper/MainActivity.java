package com.mfox.savepower.savepowerhelper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.kyleduo.switchbutton.SwitchButton;
import com.mfox.savepower.R;
import com.mfox.savepower.bean.MAppInfo;
import com.mfox.savepower.service.ScreenOffService;
import java.util.ArrayList;
import java.util.List;
import net.frederico.showtipsview.ShowTipsBuilder;
import net.frederico.showtipsview.ShowTipsView;
import net.frederico.showtipsview.ShowTipsViewInterface; 

//This project is only for study

public class MainActivity extends Activity implements CompoundButton.OnCheckedChangeListener,
		AdapterView.OnItemClickListener,
		AdapterView.OnItemLongClickListener,View.OnTouchListener{

    private List<MAppInfo> apps_List;
    private TextView tv_title,tv_top;
    private ListView mListView;
    private SwitchButton sb_all_switch;
    private PackageManager pm;
    private MyAdapter mAdapter;
    private Context mContext;
    private FrameLayout fraLayout;
    private MProgressBar mProgressBar;
    private int targeAPI;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            
        init();
    }
    
    private void init() {
        mContext=this;
        targeAPI=Build.VERSION.SDK_INT;
        pm=getPackageManager();
        mProgressBar=(MProgressBar) findViewById(R.id.MProgressBar);
        tv_top=(TextView)findViewById(R.id.tv_top_info);
        sb_all_switch= (SwitchButton) findViewById(R.id.sb_switchall_bt);
        
        MAppInfo info=new MAppInfo(mContext);
        info.setPackageName("checkallsb");
        boolean flag=info.isChecked();
        sb_all_switch.setChecked(info.isChecked());
		sb_all_switch.setEnabled(false);
        if(flag){	
            tv_top.setText(getResources().getString(R.string.tv_all_open));
        }else{
            tv_top.setText(getResources().getString(R.string.tv_all_close));
        }
         
        mAdapter=new MyAdapter(mContext);
        mAdapter.isEnable=flag;
        apps_List=new ArrayList<>();
        apps_List.add(info);
        mListView= (ListView) findViewById(R.id.mlistview);
		mListView.setEmptyView(mProgressBar);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        
        new GetAppInfoTask().execute();
    }
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
           case R.id.action_fresh:
			   new GetAppInfoTask().execute();
               break;
           case R.id.action_help:
        	   showDialog();
        	   break;
           case R.id.action_about:
           	   startActivity(new Intent(this,AboutActivity.class));
               break;
       }
        return true;
    }

    private void showDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view= View.inflate(this,R.layout.dialog_help,null);
        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog dialog=builder.create();
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.btn_comfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }	
    class GetAppInfoTask extends AsyncTask<Void, Void, List<MAppInfo>>{
    	
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
    	}
    	
		@Override
		protected List<MAppInfo> doInBackground(Void... params) {
			// TODO Auto-generated method stub			
			return getAppsList();
		}
	
		@Override
		protected void onPostExecute(List<MAppInfo> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			apps_List.clear();
			apps_List.addAll(result);
			sb_all_switch.setOnTouchListener(MainActivity.this);
			sb_all_switch.setOnCheckedChangeListener(MainActivity.this);
			mAdapter.notifyDataSetChanged();
			sb_all_switch.setEnabled(true);
			showTips();
			if(result.size()==1)
				mProgressBar.setVisibility(View.GONE);
		}
    }
    public void showTips(){
    	SharedPreferences sharedPreferences=getSharedPreferences("shared", Context.MODE_PRIVATE);
    	boolean isFirst=sharedPreferences.getBoolean("ISFIRST", true); 
    	if(!isFirst)
    		return;
    	else{
	    	final String title_all=getResources().getString(R.string.sb_all_tip_title);
	    	final String info_all=getResources().getString(R.string.sb_all_tip_info);
	    	final String title_app=getResources().getString(R.string.sb_app_tip_title);
	    	final String info_app=getResources().getString(R.string.sb_app_tip_info);
	
	    	final ShowTipsView showtips = new ShowTipsBuilder(MainActivity.this)
			.setTarget(sb_all_switch).setTitle(title_all)
			.setDescription(info_all)
			.setDelay(1000)
			.setBackgroundAlpha(128)
			.setCloseButtonColor(Color.parseColor("#ffffff"))
			.setCloseButtonTextColor(Color.GREEN)
			.build();
			showtips.show(MainActivity.this);   

			mListView.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					int index=0;
					if(apps_List.size()>2)
						index=1;
					else if(apps_List.size()<2)
						return;
					try{
						final View view=mListView.getChildAt(index).findViewById(R.id.sb_md);
							showtips.setCallback(new ShowTipsViewInterface() {
								
								@Override
								public void gotItClicked() {
									// TODO Auto-generated method stub							
									ShowTipsView showtips = new ShowTipsBuilder(MainActivity.this)
									.setTarget(view).setTitle(title_app)
									.setDescription(info_app)
									.setDelay(1000)
									.setBackgroundAlpha(128)
									.setCloseButtonColor(Color.parseColor("#ffffff"))
									.setCloseButtonTextColor(Color.GREEN)
									.build();
									showtips.show(MainActivity.this);
								}
							});		
					}catch(NullPointerException ex){
						
					}
				}
			});
	    	Editor editor=sharedPreferences.edit();
	    	editor.putBoolean("ISFIRST", false);
	    	editor.commit();
    	}
    }
    public List<MAppInfo> getAppsList(){
        List<ApplicationInfo> apps = null;
        List<MAppInfo> mappInfoList = new ArrayList<MAppInfo>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        apps = pm.getInstalledApplications(0);	
        if (apps != null){
            for (int i = 0; i < apps.size(); i++){
                if (!isSystemApp(apps.get(i), mContext)){
				    String packageName=apps.get(i).packageName;
					MAppInfo mappInfo = new MAppInfo(mContext);
					mappInfo.setPackageName(packageName);
					String pkgLable = apps.get(i).loadLabel(pm).toString();
					mappInfo.setPackageLabel(pkgLable);
					mappInfo.setMemory();
					if(targeAPI>=Build.VERSION_CODES.M)
						mappInfo.setButtery();
					else
						mappInfo.setCountService();
					Drawable icon = apps.get(i).loadIcon(pm);
					mappInfo.setIcon(icon);
					mappInfoList.add(mappInfo);                       
                }
			}
		}
        MAppInfo mAppInfo=new MAppInfo(mContext);
        mAppInfo.setPackageName("checkallsb");
        mappInfoList.add(mAppInfo);
        return mappInfoList;
    }

    public boolean isSystemApp(ApplicationInfo info, Context context) {
        PackageManager pm = context.getPackageManager();
        String packageName = info.packageName;
		if(packageName.equals(getPackageName())){
			return true;
		}
        if (packageName != null) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
                return (info != null)
                        && (packageInfo.applicationInfo != null)
                        && ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { 
	    Intent service=new Intent(this, ScreenOffService.class);
        if(isChecked){
            tv_top.setText(getResources().getString(R.string.tv_all_open));
			startService(service);
        }else{			
            tv_top.setText(getResources().getString(R.string.tv_all_close));
			stopService(service);
        }
        apps_List.get(apps_List.size()-1).setChecked(isChecked);
        mAdapter.isEnable=isChecked; 
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SwitchButton sb= (SwitchButton) view.findViewById(R.id.sb_md);
        boolean isChecked=sb.isChecked();
        if(mAdapter.isEnable)
            sb.setChecked(!isChecked);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }
   
    class MyAdapter extends BaseAdapter{
        private Context context;
        private boolean isEnable;
        public MyAdapter(Context context){
            this.context=context;
        }
        @Override
        public int getCount() {
            return apps_List.size()-1;
        }

        @Override
        public MAppInfo getItem(int position) {
            return apps_List.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView==null){
                holder=new ViewHolder();
                convertView=LayoutInflater.from(context).inflate(R.layout.appinfo_item,parent,false);
                holder.iv_icon=(ImageView) convertView.findViewById(R.id.img_icon);
                holder.tv_packageName=(TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_className=(TextView) convertView.findViewById(R.id.tv_info);
                holder.sb_button=(SwitchButton) convertView.findViewById(R.id.sb_md);
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
            holder.sb_button.setTag(position);
            final MAppInfo appInfo=apps_List.get(position);
            holder.sb_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    appInfo.setChecked(isChecked);
                }
            });
            holder.iv_icon.setImageDrawable(appInfo.getIcon());
            holder.sb_button.setChecked(appInfo.isChecked());
            holder.tv_packageName.setText(appInfo.getPackageLabel());
            String info = null;
            if(targeAPI>=Build.VERSION_CODES.M)
            	info=String.format(context.getResources().getString(R.string.tv_info_text), appInfo.getButtery(),appInfo.getMemory());
            else
				info = String.format(context.getResources().getString(R.string.tv_info_text_lowtarget),appInfo.getCountProcess(),appInfo.getCountService(),appInfo.getMemory());
            holder.tv_className.setText(info);
            holder.sb_button.setEnabled(isEnable);
            return convertView;
        }
    }
    static class ViewHolder{
        public ImageView iv_icon;
        public TextView tv_packageName;
        public TextView tv_className;
        public SwitchButton sb_button;
    }
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		int action=event.getAction();
		switch (v.getId()) {	
		case R.id.sb_switchall_bt:
			if(action==MotionEvent.ACTION_DOWN||action==MotionEvent.ACTION_MOVE){
		    	if(apps_List.size()<2){
		    		Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_no_app), Toast.LENGTH_SHORT).show();
		    		return true;
		    	}
			}			
			break;
		default:
			break;
		}
		return false;
	}
}
