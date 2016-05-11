#include"control.h"
#include"util.h"
#include<QDebug>
Control::Control(QObject *obj)
{
    udp = new QUdpSocket;
    udp->bind(QHostAddress::AnyIPv4,SERVER_PORT);
    udp->joinMulticastGroup(QHostAddress("224.0.0.3"));
    server = NULL;
    connect(udp, SIGNAL(readyRead()),this, SLOT(udpReadData()));
    connect(this,SIGNAL(startTcpServer()),this,SLOT(startTcpServerSolt()));
}

void Control::udpReadData()
{
    QString * pcInfo = new QString;
    while(udp->hasPendingDatagrams())
    {
        quint32 datagramSize = udp->pendingDatagramSize();
        QByteArray buf(datagramSize, 0);
        udp->readDatagram(buf.data(), buf.size());
        qDebug() << "Udp2" <<buf;
        QVector<QString> * _ip = Util::getIP();
        for(int i =0;i<_ip->count();i++){
            pcInfo->append(_ip->at(i));
            pcInfo->append("##");
        }
        qDebug()<<pcInfo->toUtf8()<<endl;
        emit this->startTcpServer();
        udp->writeDatagram(pcInfo->toUtf8(), QHostAddress(buf.data()),CLIENT_PORT);
    }
}

void Control::startTcpServerSolt()
{
    if(server== NULL)
        server = new QTcpServer;
    qDebug()<<Util::getIP()->at(0);
    server->listen(QHostAddress(Util::getIP()->at(0)),TCP_PORT);
    connect(server,SIGNAL(newConnection()),this,SLOT(slotNetConnection()));
}

void Control::slotNetConnection()
{
    // 判断是否有未处理的连接
    while(server->hasPendingConnections())
    {
        // 调用nextPeddingConnection去获得连接的socket
        socket = server->nextPendingConnection();

        Util::tb->append("New connection ....");
        qDebug()<<"New connection ...."<<endl;
        // 为新的socket提供槽函数，来接收数据
        connect(socket, SIGNAL(readyRead()),
                this, SLOT(slotReadyRead()));
    }
}

void Control::slotReadyRead()
{
    // 接收数据，判断是否有数据，如果有，通过readAll函数获取所有数据
    while(socket->bytesAvailable() > 0)
    {
        QByteArray buf = socket->readAll();
        Util::tb->append(buf.data());
         qDebug()<<buf.data();
    }
}
