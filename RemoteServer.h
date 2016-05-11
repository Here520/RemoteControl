#ifndef REMOTESERVER_H
#define REMOTESERVER_H

#include<QWidget>
#include<QTextBrowser>

#include<QVBoxLayout>
#include<QPushButton>
#include<QHBoxLayout>>
class RemoteServer : public QWidget
{
    Q_OBJECT
public:
    explicit RemoteServer(QWidget *parent = 0);
    QPushButton * bt1;
    QPushButton * bt2;
    QPushButton * bt3;
    QPushButton * bt4;
signals:

public slots:
    void test1();
    void test2();
    void test3();
    void test4();
};

#endif // REMOTESERVER_H
