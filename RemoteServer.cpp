#include "RemoteServer.h"
#include<QDebug>
#include<QObject>
#include<QThread>
#include"interface.h"
#include"util.h"
RemoteServer::RemoteServer(QWidget *parent)
    : QWidget(parent)
{
    QVBoxLayout* layout = new QVBoxLayout(this);
    layout->addWidget(Util::tb = new QTextBrowser(),1);
    bt1 = new QPushButton("测试1");
    bt2 = new QPushButton("测试2");
    bt3 = new QPushButton("测试3");
    bt4 = new QPushButton("测试4");

    layout->addWidget(bt1);
    layout->addWidget(bt2);
    layout->addWidget(bt3);
    layout->addWidget(bt4);

    QObject::connect(bt1,SIGNAL(clicked()),this,SLOT(test1()));
    QObject::connect(bt2,SIGNAL(clicked()),this,SLOT(test2()));
    QObject::connect(bt3,SIGNAL(clicked()),this,SLOT(test3()));
    QObject::connect(bt4,SIGNAL(clicked()),this,SLOT(test4()));


}

void RemoteServer::test1()
{
    qDebug()<<"test1()"<<"LeftDownAndUp";
    Interface::mouseLeftDown();
    Interface::mouseLeftUp();
}

void RemoteServer::test2()
{
    qDebug()<<"test2()";
    Interface::mouseRightDown();
    Interface::mouseRightUp();
}

void RemoteServer::test3()
{
    static int i = 0;
    qDebug()<<"test3()";
    Interface::mouseMoveTo(i,i);
    i+=10;


}

void RemoteServer::test4()
{
    qDebug()<<"test4()";
}

