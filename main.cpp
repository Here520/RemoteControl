#include<QPoint>
#include<QApplication>
#include<QMouseEvent>
#include<QWidget>
#include<QDesktopWidget>
#include"RemoteServer.h"
#include<QRect>
#include<QDebug>
#include<QTimer>
#include<QObject>
#include<QHostInfo>
#include<QAbstractSocket>
#include<QNetworkInterface>
#include<QNetworkAddressEntry>
#include<QList>
#include"control.h"
#include"interface.h"
#include"util.h"
void test2()
{
    //2.通过QNetworkInterface类来获取本机的IP地址和网络接口信息。
    //QNetworkInterface类提供了程序所运行时的主机的IP地址和网络接口信息的列表。
    //在每一个网络接口信息中都包含了0个或多个IP地址，而每一个IP地址又包含了和它相关的子网掩码和广播地址，
     //它们三者被封装在一个QNetworkAddressEntry对象中。网络接口信息中也提供了硬件地址信息。
    //我们将widge.cpp构造函数中以前添加的代码删除，然后添加以下代码。
    QList<QNetworkInterface> list = QNetworkInterface::allInterfaces();
        //获取所有网络接口的列表
    for(int i = 0;i<list.count();i++){
        //遍历每一个网络接口
        qDebug() << "Device: "<<list.at(i).name();
        //设备名
        qDebug() << "HardwareAddress: "<<list.at(i).hardwareAddress();
        //硬件地址
        QList<QNetworkAddressEntry> entryList = list.at(i).addressEntries();
     //获取IP地址条目列表，每个条目中包含一个IP地址，一个子网掩码和一个广播地址
        for(int j=0;j<entryList.count();j++)
        {//遍历每一个IP地址条目
            qDebug()<<"IP Address: "<<entryList.at(j).ip().toString();
            //IP地址
            qDebug()<<"Netmask: "<<entryList.at(j).netmask().toString();
            //子网掩码
            qDebug()<<"Broadcast: "<<entryList.at(j).broadcast().toString();
            //广播地址
        }
    }
}
void test3(){
    QList<QHostAddress> list = QNetworkInterface::allAddresses();
        foreach (QHostAddress address, list)
        {
            if(address.protocol() == QAbstractSocket::IPv4Protocol)
            {
                //IPv4地址
                if (address.toString().contains("127.0."))
                {
                    continue;
                }
                if(address.protocol()==QAbstractSocket::IPv4Protocol)
                    qDebug() << address.toString();
            }
        }
}


int main(int argc, char* argv[]){


    QApplication app(argc,argv);
    QDesktopWidget* deskWidget = app.desktop();
    QRect screenRect  = deskWidget->screenGeometry();
    screen_width = screenRect.width();
    screen_height = screenRect.height();
    RemoteServer rs;
    rs.setMaximumSize(320,480);
    rs.setMinimumSize(320,480);
    rs.show();

    /*QTimer* timer = new QTimer;
    QObject::connect(timer, SIGNAL(timeout()), &rs, SLOT(test3()));
    timer->setInterval(100);
    timer->start();*/

    qDebug()<<"screen_width:"<<screen_width<<"\nscreen_height:"<<screen_height;
    //test1();
    //test2();
    //test3();
    QString * pcInfo = new QString;
    QVector<QString> * _ip = Util::getIP();
    for(int i =0;i<_ip->count();i++){
        pcInfo->append(_ip->at(i));
        pcInfo->append("##");
    }
    qDebug()<<pcInfo<<endl;
    new Control();
    return app.exec();

}

