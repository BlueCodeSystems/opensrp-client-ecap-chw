package com.bluecodeltd.ecap.chw.view_holder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.bluecodeltd.ecap.chw.util.ServiceRegisterStatus.Tone;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Binds the shared register row (layout/register_row.xml), and formats what every register shows the same
 * way: dates as "6 Aug 2026", sex and age as "Female · 15y".
 */
public class RegisterRow {

    private static final DateTimeFormatter SHOWN = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter[] STORED = {
            DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d-M-yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH),
    };

    public final View itemView;
    private final View accent;
    private final View avatar;
    private final ImageView icon;
    private final TextView name;
    private final TextView id;
    private final TextView summary;
    private final TextView detail;
    private final TextView status;
    private final TextView due;
    private final View chips;
    public final LinearLayout trailing;
    public final TextView action;

    public RegisterRow(View itemView) {
        this.itemView = itemView;
        accent = itemView.findViewById(com.bluecodeltd.ecap.chw.R.id.row_accent);
        avatar = itemView.findViewById(com.bluecodeltd.ecap.chw.R.id.row_avatar);
        icon = itemView.findViewById(com.bluecodeltd.ecap.chw.R.id.row_icon);
        name = itemView.findViewById(com.bluecodeltd.ecap.chw.R.id.row_name);
        id = itemView.findViewById(com.bluecodeltd.ecap.chw.R.id.row_id);
        summary = itemView.findViewById(com.bluecodeltd.ecap.chw.R.id.row_summary);
        detail = itemView.findViewById(com.bluecodeltd.ecap.chw.R.id.row_detail);
        status = itemView.findViewById(com.bluecodeltd.ecap.chw.R.id.row_status);
        due = itemView.findViewById(com.bluecodeltd.ecap.chw.R.id.row_due);
        trailing = itemView.findViewById(com.bluecodeltd.ecap.chw.R.id.row_trailing);
        action = itemView.findViewById(com.bluecodeltd.ecap.chw.R.id.row_action);
        chips = itemView.findViewById(com.bluecodeltd.ecap.chw.R.id.row_chips);
        // Optional parts start hidden, so a register that never sets one leaves no gap for it
        for (TextView optional : new TextView[]{id, summary, detail, status, due, action}) {
            optional.setText("");
            optional.setVisibility(View.GONE);
        }
        fitChips();
    }

    /** The register's icon on its soft tint, as in the side menu. */
    public RegisterRow avatar(@DrawableRes int iconRes, @ColorRes int iconColor, @ColorRes int background) {
        Context context = itemView.getContext();
        avatar.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, background)));
        icon.setImageResource(iconRes);
        icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, iconColor)));
        return this;
    }

    public RegisterRow name(String text) {
        name.setText(text == null ? "" : text.trim());
        return this;
    }

    public RegisterRow id(String text) {
        return show(id, text);
    }

    public RegisterRow summary(String text) {
        return show(summary, text);
    }

    public RegisterRow detail(String text) {
        return show(detail, text);
    }

    public RegisterRow stripe(Tone tone) {
        accent.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), tone.stripe));
        return this;
    }

    public RegisterRow status(String text, Tone tone) {
        return pill(status, text, tone);
    }

    public RegisterRow due(String text, Tone tone) {
        return pill(due, text, tone);
    }

    /** The outlined green action on the right, or none when {@code text} is empty. */
    public RegisterRow action(String text) {
        show(action, text);
        return fitChips();
    }

    private RegisterRow show(TextView view, String text) {
        boolean empty = text == null || text.trim().isEmpty();
        view.setText(empty ? "" : text.trim());
        view.setVisibility(empty ? View.GONE : View.VISIBLE);
        return this;
    }

    private RegisterRow pill(TextView view, String text, Tone tone) {
        show(view, text);
        view.setBackgroundResource(tone.pill);
        view.setTextColor(ContextCompat.getColor(view.getContext(), tone.text));
        return fitChips();
    }

    /**
     * Hides the chip line when it has nothing to show, and lines the second chip up with the text when the
     * first is absent. Rows that fill the chips themselves call this after doing so.
     */
    public RegisterRow fitChips() {
        boolean anyChip = status.getVisibility() == View.VISIBLE || due.getVisibility() == View.VISIBLE;
        boolean extras = trailing.getVisibility() == View.VISIBLE && trailing.getChildCount() > 0;
        chips.setVisibility(anyChip || extras || action.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
        if (due.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) due.getLayoutParams();
            params.setMarginStart(status.getVisibility() == View.VISIBLE
                    ? Math.round(6 * itemView.getResources().getDisplayMetrics().density) : 0);
            due.setLayoutParams(params);
        }
        return this;
    }

    // ------------------------------------------------------------------ shared formats

    /** A stored date in any of the formats the forms use, or null. */
    public static LocalDate date(String stored) {
        if (stored == null || stored.trim().isEmpty()) return null;
        String value = stored.trim();
        // Drop a time part, e.g. "05-08-2026 13:41:06" or "2026-08-05T13:41"
        if (value.length() > 10 && (value.charAt(10) == ' ' || value.charAt(10) == 'T')) value = value.substring(0, 10);
        for (DateTimeFormatter format : STORED) {
            try {
                return LocalDate.parse(value, format);
            } catch (Exception ignored) {
                // try the next format
            }
        }
        return null;
    }

    /** "6 Aug 2026", or the stored text unchanged when it is not a date. */
    public static String shownDate(String stored) {
        LocalDate date = date(stored);
        return date != null ? date.format(SHOWN) : stored == null ? "" : stored.trim();
    }

    /** "15y", "8m" or "12d"; empty when the birthdate is unknown. */
    public static String age(String birthdate) {
        LocalDate born = date(birthdate);
        if (born == null) return "";
        Period age = Period.between(born, LocalDate.now());
        if (age.getYears() > 0) return age.getYears() + "y";
        if (age.getMonths() > 0) return age.getMonths() + "m";
        return Math.max(0, age.getDays()) + "d";
    }

    /** The parts that are present, joined with a middle dot: "Female · 15y · Child of PLHIV". */
    public static String join(String... parts) {
        List<String> present = new ArrayList<>();
        for (String part : parts) {
            if (part != null && !part.trim().isEmpty()) present.add(part.trim());
        }
        return String.join(" · ", present);
    }

    /** "Female" or "Male" from the ways the forms store sex. */
    public static String sex(String stored) {
        if (stored == null) return "";
        String value = stored.trim().toLowerCase(Locale.ENGLISH);
        if (value.startsWith("f")) return "Female";
        if (value.startsWith("m")) return "Male";
        return stored.trim();
    }
}
