#ifndef CONTROL_H
#define CONTROL_H

#include <QObject>
#include<QUdpSocket>
#include<QTcpServer>
#include<QString>
#include<QTcpSocket>
#include<QByteArray>
class Control :public QObject
{
    Q_OBJECT
private:
    QUdpSocket * udp;
    QTcpServer * server;
    QTcpSocket * socket;
public:
    explicit Control(QObject *obj = 0);
    const int SERVER_PORT = 5659;
    const int CLIENT_PORT = 5657;
    const int TCP_PORT = 5658;
signals:
    void startTcpServer();

public slots:
    void udpReadData();
    void startTcpServerSolt();
    void slotNetConnection();
    void slotReadyRead();
};

#endif // CONTROL_H
