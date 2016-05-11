#include "util.h"
#include<QHostInfo>
#include<QAbstractSocket>

Util::Util()
{

}
QString * Util::ip = NULL;
QTextBrowser * Util::tb = NULL;
QVector<QString> *Util::getIP()
{
    QVector<QString>* vec = new QVector<QString>;
    QHostInfo info=QHostInfo::fromName(QHostInfo::localHostName());
    if (info.error() != QHostInfo::NoError)
    {
         qDebug() << "Lookup failed:" << info.errorString();
         vec->append(QString("ERROR"));
         return vec;
    }

    for (int i = 0;i < info.addresses().size();i++)
    {
         if(info.addresses()[i].protocol()==QAbstractSocket::IPv4Protocol){
             qDebug() << "Found address:" << info.addresses()[i].toString() << endl;
             vec->append(info.addresses()[i].toString());
             vec->append(info.localHostName());
         }
    }
    return vec;
}

