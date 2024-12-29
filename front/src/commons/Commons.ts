import dayjs from "dayjs";

export const getNearestFriday=(date: dayjs.Dayjs)=>{
    const day = date.day();
    if (day === 5) {
      return date;
    }
    if (day < 5) {
      return date.day(5);
    }
    return date.add(1, 'week').day(5);
  }