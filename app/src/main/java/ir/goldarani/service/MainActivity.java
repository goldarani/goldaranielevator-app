package ir.goldarani.service;

import android.app.*;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    private final int BG=Color.rgb(5,18,35), PANEL=Color.rgb(12,32,54), PANEL2=Color.rgb(16,42,67),
        GOLD=Color.rgb(244,185,66), TEXT=Color.rgb(245,247,251), MUTED=Color.rgb(164,184,207),
        LINE=Color.rgb(40,78,111), GREEN=Color.rgb(58,190,145), RED=Color.rgb(244,117,91);
    private LinearLayout root, body, header, nav;
    private String page="home";
    private DB db;
    private int fontScale=100;
    private boolean showCompleted=false;

    @Override public void onCreate(Bundle b){ super.onCreate(b); getWindow().setStatusBarColor(BG); getWindow().setNavigationBarColor(BG); db=new DB(this); render(); }

    private GradientDrawable shape(int color,int stroke,int radius){
        GradientDrawable d=new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius));
        if(stroke!=0)d.setStroke(dp(1),stroke); return d;
    }
    private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
    private TextView label(String s,int size,int color,boolean bold){
        TextView t=new TextView(this); t.setText(s); t.setTextColor(color); t.setTextSize(size*fontScale/100f);
        t.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL); t.setTypeface(Typeface.create("sans-serif",bold?Typeface.BOLD:Typeface.NORMAL));
        t.setTextDirection(View.TEXT_DIRECTION_RTL); t.setPadding(dp(2),dp(2),dp(2),dp(2)); return t;
    }
    private LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);return l;}
    private LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.HORIZONTAL);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    private void gap(LinearLayout l,int h){View v=new View(this);l.addView(v,new LinearLayout.LayoutParams(1,dp(h)));}
    private TextView button(String s,Runnable r){
        TextView t=label(s,14,TEXT,true);t.setGravity(Gravity.CENTER);t.setPadding(dp(14),dp(13),dp(14),dp(13));t.setBackground(shape(PANEL2,LINE,15));
        t.setOnClickListener(v->r.run()); return t;
    }
    private void goldButton(LinearLayout parent,String text,Runnable action){
        TextView b=label(text,15,BG,true);b.setGravity(Gravity.CENTER);b.setPadding(dp(16),dp(14),dp(16),dp(14));b.setBackground(shape(GOLD,0,14));
        parent.addView(b,new LinearLayout.LayoutParams(-1,-2));b.setOnClickListener(v->action.run());
    }
    private void render(){
        root=column();root.setBackgroundColor(BG);
        header=row();header.setPadding(dp(18),dp(12),dp(18),dp(12));header.setBackground(shape(PANEL,LINE,0));
        LinearLayout brand=column();TextView en=label("GOLDARANI  /  SERVICE",11,GOLD,true);en.setGravity(Gravity.RIGHT);brand.addView(en);
        TextView fa=label("سرویس‌یار گلدرانی",20,TEXT,true);brand.addView(fa);
        TextView logo=label("G",26,BG,true);logo.setGravity(Gravity.CENTER);logo.setBackground(shape(GOLD,0,18));
        header.addView(brand,new LinearLayout.LayoutParams(0,-2,1));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(54),dp(54));lp.setMargins(dp(14),0,0,0);header.addView(logo,lp);
        root.addView(header,new LinearLayout.LayoutParams(-1,-2));
        ScrollView sc=new ScrollView(this);body=column();body.setPadding(dp(18),dp(18),dp(18),dp(20));sc.setFillViewport(false);sc.addView(body);
        root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));
        buildPage();
        nav=row();nav.setPadding(dp(8),dp(8),dp(8),dp(8));nav.setBackground(shape(PANEL,LINE,22));
        String[][] items={{"⌂","خانه","home"},{"▦","پرونده‌ها","buildings"},{"＋","ثبت","register"},{"◉","بدهی‌ها","debts"},{"⚙","تنظیمات","settings"}};
        for(String[] it:items){LinearLayout cell=column();cell.setGravity(Gravity.CENTER);TextView ic=label(it[0],22,page.equals(it[2])?GOLD:MUTED,true);ic.setGravity(Gravity.CENTER);TextView tx=label(it[1],10,page.equals(it[2])?GOLD:TEXT,false);tx.setGravity(Gravity.CENTER);cell.addView(ic);cell.addView(tx);nav.addView(cell,new LinearLayout.LayoutParams(0,-2,1));cell.setOnClickListener(v->{page=it[2];render();});}
        LinearLayout.LayoutParams np=new LinearLayout.LayoutParams(-1,-2);np.setMargins(dp(10),0,dp(10),dp(8));root.addView(nav,np);
        setContentView(root);
    }
    private void section(String title){TextView t=label(title,18,TEXT,true);t.setPadding(0,dp(8),0,dp(10));body.addView(t);}
    private LinearLayout card(){LinearLayout c=column();c.setPadding(dp(16),dp(15),dp(16),dp(15));c.setBackground(shape(PANEL,LINE,20));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,0,0,dp(12));body.addView(c,p);return c;}
    private void buildPage(){
        switch(page){
            case "home": home(); break;
            case "buildings": buildings(); break;
            case "register": register(); break;
            case "debts": debts(); break;
            case "settings": settings(); break;
            case "today": services(false); break;
            case "pending": services(true); break;
            case "done": completed(); break;
            case "all": servicesAll(); break;
            default: home();
        }
    }
    private void home(){
        LinearLayout welcome=card();welcome.setBackground(shape(PANEL2,LINE,22));
        TextView title=label("مدیریت هوشمند سرویس آسانسور",21,GOLD,true);welcome.addView(title);
        gap(welcome,7);welcome.addView(label("سلام، عزیز 👋",17,TEXT,true));
        welcome.addView(label("امروز هم کارها را مرتب و سریع مدیریت کن.",13,MUTED,false));
        gap(body,8);section("دسترسی سریع");
        String[][] tiles={{"▦","ثبت پرونده ساختمان","buildings"},{"◷","سرویس‌های امروز","today"},{"✓","انجام‌شده‌ها","done"},{"▤","همه پرونده‌ها","all"},{"◉","بدهی‌ها","debts"},{"⚙","تنظیمات","settings"}};
        for(int i=0;i<tiles.length;i+=2){
            LinearLayout r=row();LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,-2);rp.setMargins(0,0,0,dp(10));body.addView(r,rp);
            for(int j=i;j<Math.min(i+2,tiles.length);j++){final String dest=tiles[j][2];LinearLayout c=column();c.setGravity(Gravity.CENTER);c.setPadding(dp(8),dp(17),dp(8),dp(17));c.setBackground(shape(PANEL,LINE,18));
                TextView ic=label(tiles[j][0],25,GOLD,true);ic.setGravity(Gravity.CENTER);c.addView(ic);TextView tx=label(tiles[j][1],13,TEXT,true);tx.setGravity(Gravity.CENTER);c.addView(tx);
                LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(0,dp(104),1);if(j>i)cp.setMargins(dp(6),0,0,0);else cp.setMargins(0,0,dp(6),0);r.addView(c,cp);c.setOnClickListener(v->{page=dest;render();});}
        }
        section("خلاصه وضعیت");
        LinearLayout r=row();body.addView(r);
        stat(r,"پرونده‌ها",String.valueOf(db.count("buildings")));
        stat(r,"سرویس‌های ثبت‌شده",String.valueOf(db.count("services")));
    }
    private void stat(LinearLayout row,String title,String value){LinearLayout c=column();c.setPadding(dp(12),dp(12),dp(12),dp(12));c.setBackground(shape(PANEL,LINE,16));TextView n=label(value,22,GOLD,true);n.setGravity(Gravity.CENTER);c.addView(n);TextView t=label(title,11,MUTED,false);t.setGravity(Gravity.CENTER);c.addView(t);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1);p.setMargins(dp(4),0,dp(4),0);row.addView(c,p);}
    private EditText field(LinearLayout parent,String hint){EditText e=new EditText(this);e.setSingleLine(true);e.setTextColor(TEXT);e.setHintTextColor(MUTED);e.setHint(hint);e.setTextSize(15);e.setPadding(dp(13),dp(10),dp(13),dp(10));e.setBackground(shape(PANEL2,LINE,13));e.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);e.setTextDirection(View.TEXT_DIRECTION_RTL);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,0,0,dp(10));parent.addView(e,p);return e;}
    private void buildings(){
        section("پرونده‌های ساختمان");LinearLayout c=card();goldButton(c,"＋ ثبت ساختمان جدید",()->{page="register";render();});
        Cursor cur=db.getReadableDatabase().rawQuery("SELECT id,name,address,phone FROM buildings ORDER BY id DESC",null);
        if(!cur.moveToFirst()){card().addView(label("هنوز ساختمانی ثبت نشده است.",14,MUTED,false));}
        else do{final int id=cur.getInt(0);LinearLayout item=card();item.addView(label(cur.getString(1),16,TEXT,true));if(!cur.isNull(2))item.addView(label("آدرس: "+cur.getString(2),12,MUTED,false));if(!cur.isNull(3))item.addView(label("تلفن: "+cur.getString(3),12,MUTED,false));item.addView(button("＋ ثبت سرویس برای این ساختمان",()->{page="register";render();}));}while(cur.moveToNext());cur.close();
    }
    private void register(){
        section("ثبت اطلاعات جدید");TextView note=label("اطلاعات ساختمان و سرویس را در یک فرم ثبت کن.",13,MUTED,false);body.addView(note);gap(body,8);
        LinearLayout c=card();EditText name=field(c,"نام ساختمان / مشتری *");EditText address=field(c,"آدرس ساختمان");EditText phone=field(c,"شماره تماس");EditText details=field(c,"شرح سرویس");EditText fee=field(c,"مبلغ سرویس (تومان)");EditText date=field(c,"تاریخ سرویس (مثلاً ۱۴۰۵/۰۷/۳۰)");
        goldButton(c,"ثبت ساختمان و سرویس",()->{
            String n=name.getText().toString().trim();if(n.isEmpty()){name.setError("نام را وارد کن");return;}
            SQLiteDatabase w=db.getWritableDatabase();ContentValues b=new ContentValues();b.put("name",n);b.put("address",address.getText().toString());b.put("phone",phone.getText().toString());long bid=w.insert("buildings",null,b);
            ContentValues s=new ContentValues();s.put("building_id",bid);s.put("details",details.getText().toString());s.put("fee",parseMoney(fee.getText().toString()));s.put("date",date.getText().toString().trim().isEmpty()?today():date.getText().toString().trim());s.put("done",0);w.insert("services",null,s);
            Toast.makeText(this,"ساختمان و سرویس ثبت شد",Toast.LENGTH_SHORT).show();page="buildings";render();
        });
    }
    private long parseMoney(String s){try{return Long.parseLong(s.replace(",","").replace("٬","").trim());}catch(Exception e){return 0;}}
    private String today(){return new SimpleDateFormat("yyyy/MM/dd",Locale.getDefault()).format(new Date());}
    private void services(boolean pending){
        section(pending?"سرویس‌های معوق / انجام‌نشده":"سرویس‌های امروز");listServices(pending? "pending":"today");
    }
    private void completed(){section("سرویس‌های انجام‌شده");listServices("done");}
    private void servicesAll(){section("تمام سرویس‌ها");listServices("all");}
    private void listServices(String mode){
        String sql="SELECT s.id,b.name,s.details,s.fee,s.date,s.done FROM services s LEFT JOIN buildings b ON b.id=s.building_id";
        if(mode.equals("done"))sql+=" WHERE s.done=1";else if(mode.equals("pending"))sql+=" WHERE s.done=0";else if(mode.equals("today"))sql+=" WHERE s.date=? AND s.done=0";
        sql+=" ORDER BY s.id DESC";
        Cursor cur=mode.equals("today")?db.getReadableDatabase().rawQuery(sql,new String[]{today()}):db.getReadableDatabase().rawQuery(sql,null);
        if(!cur.moveToFirst()){LinearLayout c=card();c.addView(label("موردی برای نمایش وجود ندارد.",14,MUTED,false));cur.close();return;}
        do{final int id=cur.getInt(0),done=cur.getInt(5);LinearLayout c=card();c.addView(label(cur.getString(1)==null?"بدون نام":cur.getString(1),16,TEXT,true));c.addView(label(cur.getString(2)==null?"":cur.getString(2),13,MUTED,false));c.addView(label("تاریخ: "+cur.getString(4)+"     مبلغ: "+cur.getLong(3)+" تومان",12,GOLD,true));
            if(done==0)c.addView(button("علامت‌گذاری به‌عنوان انجام‌شده",()->{ContentValues cv=new ContentValues();cv.put("done",1);db.getWritableDatabase().update("services",cv,"id=?",new String[]{String.valueOf(id)});render();}));
            else c.addView(label("✓ انجام شده",13,GREEN,true));
        }while(cur.moveToNext());cur.close();
    }
    private void debts(){
        section("بدهی‌ها");LinearLayout c=card();EditText who=field(c,"نام مشتری / ساختمان");EditText amount=field(c,"مبلغ بدهی (تومان)");EditText desc=field(c,"توضیحات");goldButton(c,"＋ ثبت بدهی",()->{if(who.getText().toString().trim().isEmpty()){who.setError("نام را وارد کن");return;}ContentValues v=new ContentValues();v.put("name",who.getText().toString().trim());v.put("amount",parseMoney(amount.getText().toString()));v.put("details",desc.getText().toString());v.put("paid",0);db.getWritableDatabase().insert("debts",null,v);render();});
        Cursor cur=db.getReadableDatabase().rawQuery("SELECT id,name,amount,details,paid FROM debts ORDER BY id DESC",null);if(!cur.moveToFirst())card().addView(label("هنوز بدهی‌ای ثبت نشده است.",14,MUTED,false));else do{final int id=cur.getInt(0),paid=cur.getInt(4);LinearLayout d=card();d.addView(label(cur.getString(1),16,TEXT,true));d.addView(label(cur.getLong(2)+" تومان",15,paid==1?GREEN:RED,true));d.addView(label(cur.getString(3)==null?"":cur.getString(3),12,MUTED,false));if(paid==0)d.addView(button("تسویه شد",()->{ContentValues v=new ContentValues();v.put("paid",1);db.getWritableDatabase().update("debts",v,"id=?",new String[]{String.valueOf(id)});render();}));else d.addView(label("تسویه‌شده",12,GREEN,true));}while(cur.moveToNext());cur.close();
    }
    private void settings(){
        section("تنظیمات");LinearLayout c=card();c.addView(label("ظاهر برنامه",16,TEXT,true));c.addView(label("اندازه متن",13,MUTED,false));
        LinearLayout r=row();TextView minus=button("−",()->{fontScale=Math.max(85,fontScale-5);render();});TextView plus=button("＋",()->{fontScale=Math.min(125,fontScale+5);render();});TextView val=label(fontScale+"٪",15,GOLD,true);val.setGravity(Gravity.CENTER);r.addView(minus,new LinearLayout.LayoutParams(0,-2,1));r.addView(val,new LinearLayout.LayoutParams(0,-2,1));r.addView(plus,new LinearLayout.LayoutParams(0,-2,1));c.addView(r);c.addView(button("بازگشت به صفحهٔ اصلی",()->{page="home";render();}));LinearLayout support=card();support.addView(label("پشتیبانی سرویس‌یار گلدرانی",15,TEXT,true));support.addView(label("۰۹۱۲-۴۶۸-۲۵۸۰",16,GOLD,true));}
    private class DB extends SQLiteOpenHelper {
        DB(Context c){super(c,"goldarani_service.db",null,1);}
        public void onCreate(SQLiteDatabase d){
            d.execSQL("CREATE TABLE buildings(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,address TEXT,phone TEXT)");
            d.execSQL("CREATE TABLE services(id INTEGER PRIMARY KEY AUTOINCREMENT,building_id INTEGER,details TEXT,fee INTEGER,date TEXT,done INTEGER DEFAULT 0)");
            d.execSQL("CREATE TABLE debts(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,amount INTEGER,details TEXT,paid INTEGER DEFAULT 0)");
        }
        public void onUpgrade(SQLiteDatabase d,int oldV,int newV){}
        int count(String table){Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM "+table,null);c.moveToFirst();int n=c.getInt(0);c.close();return n;}
    }
}
