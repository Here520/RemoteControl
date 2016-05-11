#ifndef UTIL_H
#define UTIL_H

#include <QObject>
#include<QString>
#include<QVector>
#include<QTextBrowser>
class Util
{
private:
    Util();
public:
    static QVector<QString>* getIP();
    static QString * ip;
    static QTextBrowser * tb;
};

#endif // UTIL_H
