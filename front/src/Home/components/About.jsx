import React from 'react'
import {motion} from 'framer-motion';
import { slideUpVariants ,zoomInVariants} from './animation';
const About = () => {
  return (
    <div id='אודות'className='lg:w-[80%] w-[90%] m-auto py-[60px] flex lg:flex-row flex-col items-start gap-[50px] justify-between'>
      <motion.div
       initial="hidden" whileInView="visible" variants={slideUpVariants} className=' lg:w-[60%] w-full flex flex-col justify-center items-start gap-6'>
        <motion.h1 variants={slideUpVariants} className='text-red-500 text-2xl' >
          חזון
        </motion.h1>
        <motion.h1 variants={slideUpVariants} className='text-white uppercase text-[40px] font-bold' >
"תן לו משלו, שאתה ושלך-שלו" (פרק שלישי פרקי אבות)       </motion.h1>
        <div  className='w-[120px] h-[6px] bg-red-500'></div>
        <p className='text-3xl italic text-gray-50 mt-[60px]'>אני מאמינה שההתנדבות היא כלי משמעותי בחיזוק הקשר בין האזרח לקהילה
ובהעצמת תחושת המסוגלות של המתנדב. כל התנדבות באשר היא מחוללת שינוי,
וכשישנה עשייה, נתינה וחסד הלב מתרחב ובכך אנחנו מייצרים מעגלי השפעה
לטובה אחד על השנייה.</p>
      </motion.div>
      <motion.div
       variants={slideUpVariants} className='lg:w-[40%] w-full flex flex-col justify-center items-start gap-6'
      >
        <p className='text-white text-lg text-justify'>
          "להשביע את הלב" הוקם בשנת 2015 ופועל בכל ימות השנה בכלל ובחגים וסופי
שבוע בפרט ע"י גיוס תרומות.

ב"להשביע את הלב" פועלים למען משפחות, קשישים, פגועי נפש, אמהות חד הוריות, בעלי נכות נפשית ופיזית כאחד.

המיזם מלווה משפחות ועוזר לאלה שנקלעו למשברים ורוצים להשתקם ולהתקדם בחייהם.
המיזם דואג שקשישים רבים יעבירו את השבת והחגים עם אוכל חם וטרי,
ואף דואג למצות איתם את הזכויות המגיעות להם.

כיום גודל מערך ההתנדבות שלנו מתפרס לכ200 בתי אב בניהם קשישים ומשפחות קשיי יכולת. 
אנחנו פונים לאוכלוסיות במצוקה ומנסים לעבור עימם תהליך שיקום הכולל הספקת ארוחה חמה, אוזן קשבת והכוונה בערוצים הנכונים. 
יחד עם זאת אחת לחודש נאספים סלי מזון עשירים שמסופקים למשפחות (חלבי, בשרי, יבשים, ירקות ופירות).
        </p>
        <motion.button
  variants={zoomInVariants}
  className='bg-red-500 hover:bg-white hover:text-black px-10 py-3 rounded-lg text-black font-bold'
  >
קרא עוד
  </motion.button>
        </motion.div>
    </div>
  )
}

export default About;