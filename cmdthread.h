#ifndef CMDTHREAD_H
#define CMDTHREAD_H

#include <QObject>
#include <QTcpSocket>
#include<QThread>
class CmdThread : public QThread
{
    Q_OBJECT
private:
    QTcpSocket * socket;
    int   cmd_buf_fill;
    uchar cmd_buf[16];
public:
    explicit CmdThread(QTcpSocket * socket, QObject *parent = 0);
protected:
    void run();
public slots:
    void newData();
    void newCommand();

    void cmdMouseMoveTo();
    void cmdMouseLeftPress();
    void cmdMouseRightPress();
};

#endif // CMDTHREAD_H
