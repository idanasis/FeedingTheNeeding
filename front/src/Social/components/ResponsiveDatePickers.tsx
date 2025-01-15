import * as React from 'react';
import dayjs from 'dayjs';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { getNearestFriday } from '../../commons/Commons';


const   ResponsiveDatePickers=({ onDateChange }: { onDateChange: (date: dayjs.Dayjs | null) => void })=> {
const handleDisableDate = (date: dayjs.Dayjs) => {
    return date.day() !== 5;
  }
  const handleDisableMonth = (date: dayjs.Dayjs) => {
    const chose = dayjs(); 
    const isFutureYear = chose.year() > date.year();
    const isMoreThanOneMonthAhead =
      chose.year() === date.year() && chose.month() -date.month()  > 1;
    return isFutureYear || isMoreThanOneMonthAhead;
  };
  

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
     <DatePicker defaultValue={getNearestFriday(dayjs(Date.now()))} shouldDisableDate={handleDisableDate} shouldDisableMonth={handleDisableMonth}onChange={onDateChange} format='DD/MM/YYYY'/>
    </LocalizationProvider>
  );
}
export default ResponsiveDatePickers;