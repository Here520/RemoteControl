#include "cmdthread.h"
#include "consts.h"
CmdThread::CmdThread(QTcpSocket *socket, QObject *parent)
{
    this->socket = socket;
}
void CmdThread::run()
{
    QThread::run();
}

void CmdThread::newData()
{
    while(true)
    {
        int r = socket->read((char*)(cmd_buf + cmd_buf_fill), 8 - cmd_buf_fill);
        if(r <= 0)
            return;
        cmd_buf_fill += r;
        if(cmd_buf_fill == 8)
        {
            newCommand();
            cmd_buf_fill = 0;
        }
    }

}

void CmdThread::newCommand()
{
    //qDebug()<<"new command";
    int cmd = cmd_buf[0];
    switch(cmd)
    {
        case CMD_MOUSE_MOVE_TO:
            cmdMouseMoveTo();
            break;
        case CMD_MOUSE_LEFT_PRESS:
            cmdMouseLeftPress();
            break;
        case CMD_MOUSE_RIGHT_PRESS:
            cmdMouseRightPress();
            break;
    }
}


void CmdThread::cmdMouseLeftPress()
{

}

void CmdThread::cmdMouseRightPress()
{

}

void CmdThread::cmdMouseMoveTo(){

}
