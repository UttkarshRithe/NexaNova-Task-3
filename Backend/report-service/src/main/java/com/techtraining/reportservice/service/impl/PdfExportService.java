package com.techtraining.reportservice.service.impl;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.borders.Border;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class PdfExportService {

    public static final DeviceRgb HEADER_DARK_COLOR = new DeviceRgb(24, 23, 21); // #181715
    public static final DeviceRgb ACCENT_COLOR = new DeviceRgb(204, 120, 92); // #cc785c
    public static final DeviceRgb SECTION_BG = new DeviceRgb(239, 233, 222); // #efe9de
    public static final DeviceRgb BORDER_COLOR = new DeviceRgb(230, 223, 216); // #e6dfd8
    public static final DeviceRgb TEXT_PRIMARY = new DeviceRgb(20, 20, 19); // #141413
    public static final DeviceRgb TEXT_SECONDARY = new DeviceRgb(61, 61, 58); // #3d3d3a
    
    public static final DeviceRgb SUCCESS_COLOR = new DeviceRgb(93, 184, 114); // #5db872
    public static final DeviceRgb WARNING_COLOR = new DeviceRgb(212, 160, 23); // #d4a017
    public static final DeviceRgb DANGER_COLOR = new DeviceRgb(198, 69, 69); // #c64545

    public com.itextpdf.kernel.colors.Color getScoreColor(int score) {
        if (score >= 85) return SUCCESS_COLOR;
        if (score >= 70) return SUCCESS_COLOR; // Good (70-84)
        if (score >= 60) return WARNING_COLOR;
        return DANGER_COLOR;
    }

    public Cell makeBadge(String text, com.itextpdf.kernel.colors.Color bgColor, com.itextpdf.kernel.colors.Color textColor) {
        return new Cell()
                .add(new Paragraph(text)
                        .setFontSize(9)
                        .setFontColor(textColor))
                .setBackgroundColor(bgColor)
                .setBorderRadius(new BorderRadius(10))
                .setPaddingTop(2).setPaddingBottom(2)
                .setPaddingLeft(8).setPaddingRight(8)
                .setBorder(Border.NO_BORDER);
    }

    public String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return "—";
        return dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    public Cell makeScoreCell(int score, Integer previousScore) {
        Paragraph p = new Paragraph(score + " / 100")
                .setFontColor(getScoreColor(score))
                .setBold()
                .setFontSize(11);

        if (previousScore != null) {
            int diff = score - previousScore;
            String arrow = diff > 0 ? "↑ +" : "↓ ";
            com.itextpdf.kernel.colors.Color diffColor = diff > 0 ? SUCCESS_COLOR : DANGER_COLOR;
            p.add(new Text("\n" + arrow + Math.abs(diff) + " from prev round")
                    .setFontSize(9)
                    .setFontColor(diffColor));
        }
        return new Cell().add(p).setPadding(6).setBorder(Border.NO_BORDER);
    }

    public Table createProgressBar(double score, com.itextpdf.kernel.colors.Color barColor) {
        Table bar = new Table(UnitValue.createPercentArray(new float[]{(float) score, (float) (100 - score)}))
                .setWidth(UnitValue.createPercentValue(100));
        bar.addCell(new Cell().setHeight(6).setBackgroundColor(barColor).setBorder(Border.NO_BORDER));
        bar.addCell(new Cell().setHeight(6).setBackgroundColor(BORDER_COLOR).setBorder(Border.NO_BORDER));
        return bar;
    }
}
