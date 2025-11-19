import React, { useState } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';

export default function MyDatePicker() {
  const [startDate, setStartDate] = useState(new Date());
  return (
    <DatePicker
      selected={startDate}
      onChange={(date) => setStartDate(date)}
      showIcon
      dateFormat="yyyy-MM-dd"
    />
  );
} 

/*
jQuery UI vs React Alternatives
jQuery UI Widget        React Equivalent (Modern Alternatives)
    
Datepicker              react-datepicker, @mui/x-date-pickers
Modal Dialog            react-modal, @mui/material/Dialog, Radix UI Dialog, Headless UI
Tabs                    @mui/material/Tabs, Radix UI Tabs, Headless UI Tabs
Accordion               @mui/material/Accordion, Radix UI Accordion, Headless UI Accordion
Autocomplete            @mui/material/Autocomplete, Downshift, React Select
Draggable               react-draggable, react-beautiful-dnd
    
UI Libraries That Offer Multiple Widgets
Library	Notes
Material UI (MUI)       Full-featured, accessible, well-documented
Radix UI                Headless, accessible primitives with full styling control
Headless UI             Tailwind-friendly, no styling, just behavior
Chakra UI               Theming, styling, and good defaults out of the box

🔚 Summary
    ✅ React does not include jQuery UI-style widgets out of the box
    ✅ But it has modern, flexible alternatives via third-party libraries
    ✅ These libraries are typically more accessible, customizable, and mobile-friendly

Headless + Tailwind-Friendly = Maximum Flexibility
    If a library is headless and Tailwind-friendly (like Headless UI or Radix UI):
        You get full control over layout and design
        You can easily match your app’s branding
        The component behavior is accessibility-compliant out of the box

Headless - Component gives you behavior only, no visuals
Tailwind-friendly - Component is easy to style using Tailwind CSS utility classes  - a utility-first CSS framework.

So Tailwind-friendly components:
    Use utility classes instead of prebuilt themes
    Are unstyled by default (or minimally styled)
    Let you control spacing, fonts, borders, etc.

Compare: Styled vs Headless
Feature             Styled UI (e.g., MUI)           Headless UI (e.g., HeadlessUI, Radix)
Includes styles?	✅ Yes                           ❌ No — you style it
Uses Tailwind?      ❌ No (conflicts sometimes)      ✅ Yes, perfect match
Customizability     ⚠️ Limited via theme             ✅ Total control
Use case            Fast prototyping                Precise, branded UIs
 */
