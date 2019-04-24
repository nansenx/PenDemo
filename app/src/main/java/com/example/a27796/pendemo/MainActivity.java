package com.example.a27796.pendemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    private ListView lv_bluetooth;
    private Button searchbt;
    private Button linkbluet;
    private List<String> bName = new ArrayList<>();
    private List<String> bAdd = new ArrayList<>();

    private BluetoothAdapter mBluetoothAdapter;
    private List<String> devices = new ArrayList<>();
    ArrayAdapter<String> mArrayAdapter;

    //注册广播查找蓝牙设备
    private final BroadcastReceiver mReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //获得 BluetoothDevice
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 发现设备
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    //向mArrayAdapter中添加设备信息
                    devices.add(device.getName() + ":" + device.getAddress());
                    mArrayAdapter.notifyDataSetChanged();//更新适配器
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //已搜素完成
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        choose();
    }
    //初始化控件
    public void initView(){
        lv_bluetooth = findViewById(R.id.lv_bluetooth);
        searchbt = findViewById(R.id.searchbt);
        searchbt.setOnClickListener(onclick1);
        linkbluet = findViewById(R.id.linkbluet);
        linkbluet.setOnClickListener(onclick1);
    }

    View.OnClickListener onclick1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.searchbt:
                    FindBlueTooth();
                    break;
                case R.id.linkbluet:
                    Intent intent = new Intent(MainActivity.this,BluetoothActivity.class);
                    startActivity(intent);
            }
        }
    };
    AdapterView.OnItemClickListener onitemclick1 = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String add = bAdd.get(position);
            String name = bName.get(position);
            Intent i = new Intent(MainActivity.this, BluetoothActivity.class);
            i.putExtra("add",add);
            i.putExtra("name",name);
            i.putExtra("TYPE",TYPE);
            startActivity(i);
        }
    };
    //创建长按弹窗
    private void ItemOnLongClick1(){
        lv_bluetooth.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0,0,0,"删除");
                menu.add(0,1,0,"更改");
            }
        });
    }
    public boolean onContextItemSelected(MenuItem menuItem){
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        switch (menuItem.getItemId()){
            case 0:
                removeItem(menuInfo);
                break;
            case 1:
                changeItem(menuInfo);
                break;
        }
        return super.onContextItemSelected(menuItem);
    }
    //打印所有已配对设备到lv_bluetooth
    public void FindBlueTooth(){
        if (mArrayAdapter != null){
            mArrayAdapter.clear();
            mArrayAdapter.notifyDataSetChanged() ;
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //获取已经配对的蓝牙设备
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                int i=0;
                devices.add(device.getName() + ":"+ device.getAddress());
                bName.add(device.getName());
                bAdd.add(device.getAddress());
                i=i+1;
            }
        }
        mArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,devices);
        mArrayAdapter.notifyDataSetChanged();//更新适配器
        lv_bluetooth.setAdapter(mArrayAdapter);
        lv_bluetooth.setOnItemClickListener(onitemclick1);
        ItemOnLongClick1();
    }
    //删除功能
    public void removeItem(AdapterView.AdapterContextMenuInfo menuInfo){
        int pos=(int)lv_bluetooth.getAdapter().getItemId(menuInfo.position);
        if(devices.remove(pos)!=null){//这行代码必须有
            Toast.makeText(MainActivity.this,"删除成功",Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(MainActivity.this,"删除失败",Toast.LENGTH_LONG).show();
        }
        mArrayAdapter.notifyDataSetChanged();
    }
    //更改功能
    public void changeItem(final AdapterView.AdapterContextMenuInfo menuInfo){
        final int pos=(int)lv_bluetooth.getAdapter().getItemId(menuInfo.position);

        final EditText edit = new EditText(this);
        edit.setText(devices.get(pos));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入").setView(edit)
                .setNegativeButton("返回", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if(devices.set(pos,edit.getText().toString())!=null){//这行代码必须有
                    Toast.makeText(MainActivity.this,"修改成功",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(MainActivity.this,"修改失败",Toast.LENGTH_LONG).show();
                }
                mArrayAdapter.notifyDataSetChanged();
            }
        });
        builder.show();
    }
    int TYPE;
    BluetoothMsg.ServerOrCilent serviceOrCilent2;
    private void choose(){
        final String[] items = {"服务器端","客户端"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("选择类型");
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Toast.makeText(MainActivity.this,""+items[0],Toast.LENGTH_LONG).show();
                        serviceOrCilent2=BluetoothMsg.ServerOrCilent.SERVICE;
                        TYPE = 101;
                        break;
                    case 1:
                        Toast.makeText(MainActivity.this,""+items[0],Toast.LENGTH_LONG).show();
                        serviceOrCilent2=BluetoothMsg.ServerOrCilent.CILENT;
                        TYPE = 102;
                        break;
                }
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BluetoothMsg.serviceOrCilent=serviceOrCilent2;
            }
        });
        builder.show();
    }
}
