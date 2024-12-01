package com.tictactoe.servlet;

import com.tictactoe.Field;
import com.tictactoe.Sign;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();

        Sign winner = (Sign) session.getAttribute("winner");
        if (winner != Sign.EMPTY) {
            getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        int index = getIndex(request);

        Field field = extractField(session);
        List<Sign> data = field.getFieldData();

        if (data.get(index) != Sign.EMPTY) {
            getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }
        field.getField().put(index, Sign.CROSS);
        if (checkWin(request, response, session, field)) {
            return;
        }

        int emptyFieldIndex = field.getEmptyFieldIndex();
        if (emptyFieldIndex != -1) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
        } else {
            session.setAttribute("draw", true);
            session.setAttribute("field", field);
            session.setAttribute("data", field.getFieldData());
            response.sendRedirect("/index.jsp");
            return;
        }

        if (checkWin(request, response, session, field)) {
            return;
        }

        session.setAttribute("field", field);
        session.setAttribute("data", field.getFieldData());
        response.sendRedirect("/index.jsp");

    }

    private int getIndex(HttpServletRequest request) {
        String click = request.getParameter("click");
        return Integer.parseInt(click);
    }

    private Field extractField(HttpSession session) {
        return (Field) session.getAttribute("field");
    }

    private boolean checkWin(HttpServletRequest request, HttpServletResponse response, HttpSession session, Field field) throws ServletException, IOException {
        Sign sign = field.checkWin();
        if (sign == Sign.EMPTY) {
            return false;
        }
        session.setAttribute("field", field);
        session.setAttribute("data", field.getFieldData());
        session.setAttribute("winner", sign);
        response.sendRedirect("/index.jsp");
        return true;
    }
}