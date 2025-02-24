import React, { ChangeEvent, useEffect, useState } from 'react';
import {
  DndContext,
  closestCenter,
  PointerSensor,
  useSensor,
  useSensors,
  TouchSensor,
} from '@dnd-kit/core';
import {
  SortableContext,
  verticalListSortingStrategy,
} from '@dnd-kit/sortable';
import { useDraggable,useDroppable  } from '@dnd-kit/core';
import ResponsiveDatePickers from '../../Social/components/ResponsiveDatePickers';
import { Box, Card, CardContent, Typography, Select, MenuItem, Container, Button, IconButton, Fab } from '@mui/material';
import { Visit } from '../models/Visit';
import { Route } from '../models/Route';
import "../styles/Driving.css";
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import WhatsAppIcon from '@mui/icons-material/WhatsApp';
import AddIcon from '@mui/icons-material/Add';
import { addDriverConstraints, addRoute, deleteRoute, getDriversConstraints, getNeedersHere, getPickupVisits, getRoutes, submitAllRoutes, submitRoute, updateRoute } from '../../Restapi/DrivingRestapi';
import { Donor } from '../models/Donor';
import dayjs from 'dayjs';
import { getNearestFriday } from '../../commons/Commons';
import { DriverConstraints } from '../models/DriverConstraints';
import AddDriverOption from './AddDriverOption';
import { getDonorApproved } from '../../Restapi/DrivingRestapi';
import DiveHeader from '../../GoPage/DiveHeader';
// Add these imports at the top of the file
import LocalDiningIcon from '@mui/icons-material/LocalDining';
import VolunteerActivismIcon from '@mui/icons-material/VolunteerActivism';
import DriverIcon from '@mui/icons-material/DriveEta';

import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';

const initialData = {
  routes: [
  ] as Route[],
  pickup: [] as Visit[],
  drop: [] as Visit[],
};
export interface Data {
  routes: Route[],
  pickup: Visit[],
  drop: Visit[]
}
const Draggable = ({ id, children }: { id: string; children: React.ReactNode }) => {
  const { attributes, listeners, setNodeRef, transform } = useDraggable({ id });
  
  const style = {
    transform: transform
      ? `translate3d(${transform.x}px, ${transform.y}px, 0)`
      : undefined,
    marginBottom: '8px',
    touchAction:'none',
    'Button[dataNoDrag]': {
      pointerEvents: 'auto', /* Ensure the button is still clickable */
    }
  };

  return (
    <div ref={setNodeRef} {...listeners} {...attributes} style={style} >
      {children}
    </div>
  );
};

const getVisitNamesForRoute = (route: Route) => {
  // Filter out the driver's start location if present
  
  // Create names with statuses
  const namesWithStatuses = route.visit
    .map(visit => {
      let statusText = '';
      switch(visit.status) {
        case 'Pickup':
          statusText = 'איסוף';
          break;
        case 'Deliver':
          statusText = 'חלוקה';
          break;
        case 'Start':
          statusText = 'התחלה';
          break;
        default:
          statusText = 'סטטוס לא ידוע';
      }
      return `${statusText}: ${visit.firstName} ${visit.lastName}`;
    })
    .join(', ');
  
 
  return namesWithStatuses;
};

const Droppable = ({ id, children }: { id: string; children: React.ReactNode }) => {
  const { setNodeRef } = useDroppable({ id });

  return (
    <div ref={setNodeRef} style={{touchAction:'none',minHeight: '100px', padding: '8px', border: '1px dashed gray' }}>
      {children}
    </div>
  );
};


const DrivingManager = () => {
  const [data, setData] = useState<Data>(initialData);
  const [driver,setDrivers]=useState<DriverConstraints[]>([]);
  const [date, setDate] = useState<Date>(getNearestFriday(dayjs(Date.now())).toDate());
  const [donors, setDonors] = useState<Donor[]>([]);
  const [visible,setVisible]=useState(false);
  const [minimizedRoutes, setMinimizedRoutes] = useState<{[key: number]: boolean}>({});

   const toggleRouteMinimization = (index: number) => {
    setMinimizedRoutes(prev => ({
      ...prev,
      [index]: !prev[index]
    }));
  };

  async function fetchDrivers(currentDate:Date=date) {
    try{
      const data=await getDriversConstraints(currentDate); 
      setDrivers(data)
      let donors=await getDonorApproved();
      donors=donors.filter(donor=>data.every(driver=>driver.driverId!==donor.id));
      setDonors(donors);
    }catch(err){
      alert("תקלה בהצגת הנתונים");
    }
  }
  async function getDrops(currentDate:Date=date){
    try{
      const data=await getNeedersHere(currentDate);
        const updatedData={...initialData};
        updatedData.drop=data;
        const routes=await getRoutes(date);
        updatedData.routes=routes;
        updatedData.drop = updatedData.drop.filter((visit: Visit) =>
          !routes.some((route: Route) =>
            route.visit.some((v: Visit) => v.phoneNumber === visit.phoneNumber)
          )
        ); 
        const pickup=await getPickupVisits(date);
        updatedData.pickup=pickup;
        updatedData.pickup = updatedData.pickup.filter((visit: Visit) =>
          !routes.some((route: Route) =>
            route.visit.some((v: Visit) => v.phoneNumber === visit.phoneNumber)
          )
        );       
        setData(updatedData);
      }catch(err){
        alert("תקלה בהצגת הנתונים");
      }
  }
  useEffect(() => {
          fetchDrivers();
          getDrops();
      }, [date]);
  const handleDateChange = (newDate:dayjs.Dayjs|null ) => {
          const d =newDate===null?dayjs(Date.now()).toDate():newDate.toDate();
          setDate(d);
          fetchDrivers(d);
          getDrops(d);
        };
  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: {
        distance: 10, // Prevent drag unless the pointer moves 10px
      },
      onStart: (event: { target: HTMLElement; }) => {
        const element = event.target as HTMLElement;
        if (element.getAttribute("dataNoDrag")) {
          return false; // Cancel drag for excluded elements
        }
      },
    }),
    useSensor(TouchSensor, { activationConstraint: { delay: 100, tolerance: 10 } })
  );

  const handleDragEnd = async({ active, over }: { active: any; over: any }) => {
    if (!over) return;

    const [sourceContainer, sourceIndex] = active.id.split('-') as [keyof typeof data, string];
    const [targetContainer, targetIndex] = over.id.split('-') as [keyof typeof data, string];
    if (sourceContainer === targetContainer) return;

    // Moving visit from pickup/drop to a route
    if (targetContainer === 'routes' && (sourceContainer === 'pickup' || sourceContainer === 'drop')) {
      const routeIndex = parseInt(targetIndex);
      const sourceItems = [...data[sourceContainer]];
      const movedItem = sourceItems.splice(parseInt(sourceIndex), 1)[0];
      
      const updatedRoutes = [...data.routes];
      const route=updatedRoutes[routeIndex]
      movedItem.priority=route.driverId===0?route.visit.length+1:route.visit.length;
      movedItem.note=movedItem.notes;
      route.visit.push(movedItem as Visit);
      await updateRoute(route);
      setData({
        ...data,
        [sourceContainer]: sourceItems,
        routes: updatedRoutes,
      });
    }
    else if (sourceContainer.startsWith('r') && targetContainer.startsWith('r')) {
      const [sourceContainer, sourceIndex,visit,visitIndex] = active.id.split('-') as [keyof typeof data, string,string,string];
      const [targetContainer, targetIndex,visit2,visitIndex2] = over.id.split('-') as [keyof typeof data, string,string,string];
      const updatedRoutes = [...data.routes];
      const curr=updatedRoutes[parseInt(sourceIndex)].visit.splice(parseInt(visitIndex), 1)[0];
      const destRoute=updatedRoutes[parseInt(targetIndex)];
      const sourceRoute=updatedRoutes[parseInt(sourceIndex)];
      const length=destRoute.visit.length+1;
      let newRoute: Visit[] = new Array(length);
      
      for(let i=0;i<length;i++){
        newRoute[i]=destRoute.visit[i];
      }
      for(let i=1;i<sourceRoute.visit.length;i++){
        if(sourceRoute.visit[i].priority!==i)
          sourceRoute.visit[i].priority=(sourceRoute.visit[i].priority as number) -1;
      }
      curr.priority=destRoute.driverId===0?destRoute.visit.length+1:destRoute.visit.length;;
      newRoute[length-1]=curr;
      destRoute.visit=newRoute;
      await updateRoute(sourceRoute);
      await updateRoute(destRoute);
      updatedRoutes[parseInt(targetIndex)].visit=newRoute;
      updatedRoutes[parseInt(sourceIndex)]=sourceRoute; 
      setData({
        ...data,
        routes: updatedRoutes,
      });
    }
  };

  const formatRouteForCopy = (route: Route) => {
    let text = `מסלול הנסיעה שלך:\n\n`;

    let action=``;
    
    route.visit.forEach((visit, index) => {
      if(visit.status==="Start"){
        action="התחלה";
      }
      else if(visit.status==="Pickup"){
        action="איסוף";
      }
      else if(visit.status=="Deliver"){
        action="חלוקה"
      }

      text += `תחנה ${index + 1}: ${action}\n`;
      text += `שם: ${visit.firstName} ${visit.lastName}\n`;
      text += `כתובת: ${visit.address}\n`;
      text += `טלפון: ${visit.phoneNumber}\n`;
      
     
      if (visit.note || visit.notes) {
        text += `הערות: ${visit.note || visit.notes}\n`;
      }
      if (visit.dietaryPreferences) {
        text += `העדפות תזונה: ${visit.dietaryPreferences}\n`;
      }
      text += '\n';
    });
    
    text+="שבת שלום! 3>"
    return text;
  };

   const handleCopyRoute = async (route: Route) => {
    try {
      const text = formatRouteForCopy(route);
      await navigator.clipboard.writeText(text);
      alert('המסלול הועתק בהצלחה');
    } catch (err) {
      alert('שגיאה בהעתקת המסלול');
    }
  };



 const renderVisit = (visit: Visit) => {
  const isChef = visit.status === "Pickup";
  const isNeedyPerson = visit.status === "Deliver";
  const isDriverStart = visit.status === "Start";

  return (
    <Card
      variant="outlined"
      sx={{
        height: '160px',
        backgroundColor: isChef 
          ? 'rgba(76, 175, 80, 0.1)' // Light green for chefs
          : isNeedyPerson 
            ? 'rgba(244, 67, 54, 0.1)' // Light red for needy people
            : 'rgba(33, 150, 243, 0.1)', // Light blue for driver start
        border: isChef 
          ? '1px solid rgba(76, 175, 80, 0.5)' 
          : isNeedyPerson 
            ? '1px solid rgba(244, 67, 54, 0.5)' 
            : '1px solid rgba(33, 150, 243, 0.5)',
      }}
    >
      <CardContent>
        <Box sx={{ display: 'flex', alignItems: 'center', marginBottom: '8px' }}>
          {isChef && <LocalDiningIcon color="success" sx={{ marginRight: '8px' }} />}
          {isNeedyPerson && <VolunteerActivismIcon color="error" sx={{ marginRight: '8px' }} />}
          {isDriverStart && <DriverIcon color="primary" sx={{ marginRight: '8px' }} />}
          <Typography variant="h6" fontSize={16}>
            {visit.firstName} {visit.lastName}
          </Typography>
        </Box>
        <Typography variant="body2" fontSize={11}>{visit.address}</Typography>
        <Typography variant="body2" fontSize={11}>{visit.phoneNumber}</Typography>
        {visit.startHour && visit.startHour !== "0:00" && (
          <Typography variant="body2" fontSize={11}>
            שעת התחלה/מינימלית: {visit.startHour}
          </Typography>
        )}
        {visit.endHour && visit.endHour !== "0:00" && (
          <Typography variant="body2" fontSize={11}>
            שעת הגעה/סיום: {visit.endHour}
          </Typography>
        )}
        <Typography variant="body2" fontSize={11}>
          הערות: {visit.note || visit.notes || 'אין הערות'}
        </Typography>
        {visit.additionalNotes && (
          <Typography variant="body2" fontSize={11}>
            {visit.additionalNotes}
          </Typography>
        )}
      </CardContent>
    </Card>
  );
};

  const upButton =(index:number,idx:number,route: Route)=>{
    if(idx!==0&&(idx!==1||route.driverId===undefined||route.driverId===0)){
      return <IconButton color="primary" aria-label="up" data-no-drag onClick={(e)=>{
        e.stopPropagation();
        handleUp(index,idx)}} >
      <ArrowUpwardIcon />
    </IconButton>
    }
    return null;
  }
  const downButton =(index:number,idx:number,route: Route)=>{
    if(idx!==route.visit.length-1&&(idx!==0||route.driverId===undefined||route.driverId===0)){
      return <IconButton color="primary" aria-label="down" data-no-drag onClick={(e) => {
        e.stopPropagation();
        handleDown(index,idx)
      }}>
      <ArrowDownwardIcon />
    </IconButton>
    }
    return null;
  }
  const handleUp =async(index:number,idx: number)=>{
    const updatedRoutes = [...data.routes];
    const length=updatedRoutes[index].visit.length;
    const currRoute=Array.from(updatedRoutes[index].visit);
    const curr=updatedRoutes[index].visit.splice(idx, 1)[0];
    curr.priority=curr.priority!-1;
    let newRoute: Visit[] = new Array(length);
    let next=updatedRoutes[index].visit.splice(idx-1, 1)[0];
    next.priority=next.priority!+1;
    for(let i=0;i<length;i++){
      if (i===idx-1){
        newRoute[i]=curr;
      }else if(i===idx){
        newRoute[i]=next;
      }
      else{
      newRoute[i]=currRoute[i];
      }
    }
    const route=updatedRoutes[index];
    route.visit =newRoute;
    await updateRoute(route);
    updatedRoutes[index]=route;
    setData({
      ...data,
      routes: updatedRoutes,
    });
  }
  const handleDown =async(index:number,idx: number)=>{
    const updatedRoutes = [...data.routes];
    const length=updatedRoutes[index].visit.length;
    const currRoute=Array.from(updatedRoutes[index].visit);
    const curr=updatedRoutes[index].visit.splice(idx, 1)[0];
    curr.priority=curr.priority!+1;
    let newRoute: Visit[] = new Array(length);
    let next=updatedRoutes[index].visit.splice(idx, 1)[0];
    next.priority=next.priority!-1;
    for(let i=0;i<length;i++){
      if (i===idx+1){
        newRoute[i]=curr;
      }else if(i===idx){
        newRoute[i]=next;
      }
      else{
      newRoute[i]=currRoute[i];
      }
    }
    const route=updatedRoutes[index];
    route.visit =newRoute;
    await updateRoute(route);
    updatedRoutes[index]=route;
    setData({
      ...data,
      routes: updatedRoutes,
    });
  }
  const handleAddRoute = async() => {
    const route=await addRoute(date);
    const updatedRoutes = [...data.routes];
    updatedRoutes.push(route);
    setData({
      ...data,
      routes: updatedRoutes,
    });
  }
  const handleDriverChange = async(e: React.ChangeEvent<{ value: unknown }>, index: number) => {
    try{
    const route=data.routes[index] as Route;
    const updatedRoutes = [...data.routes];
    if(route.driverId!==0&&!isNaN(route.driverId!)){
      route.visit.shift();
    }
    route.driver = driver.find(d => d.driverId === parseInt(e.target.value as string)) as DriverConstraints;
    route.driverId = parseInt(e.target.value as string);
    if(isNaN(route.driverId)){
      updatedRoutes[index]=route;
    setData({ ...data, routes: updatedRoutes });
      return;
    }
    const visit={address:updatedRoutes[index].driver?.startLocation as string,firstName:updatedRoutes[index].driver?.driverFirstName as string,lastName:updatedRoutes[index].driver?.driverLastName as string,phoneNumber:updatedRoutes[index].driver?.driverPhone as string,endHour:updatedRoutes[index].driver?.endHour,note:updatedRoutes[index].driver?.requests,status:"Start",priority:0,startHour:updatedRoutes[index].driver?.startHour};
    route.visit.unshift(visit);
    await updateRoute(route);
    updatedRoutes[index]=route;
    setData({ ...data, routes: updatedRoutes });
    }catch(err){
      alert('תקלה בשמירת הנתונים');
    }
  }
  const handlePublish = async(index:number) => {
    try{
    await submitRoute(data.routes[index]);
    const updatedData={...data};
    if(updatedData.routes[index].driverId===0){
      alert('נא לבחור נהג');
      return;
    }
    updatedData.routes[index].submitted=true;
    setData(updatedData);
    }catch(err){
      alert('תקלה בשמירת הנתונים');
    }
  }
  const handlePublishAll = async() => {
    try{
    
    const updatedData={...data};
    if(updatedData.routes.some(route=>route.driverId===0)){
      alert('נא לבחור נהגים לכל המסלולים או למחוק אותם');
      return;
    }
    await submitAllRoutes(date);
    updatedData.routes.forEach(route=>route.submitted=true);
    setData(updatedData);
    alert('מסלולים פורסמו בהצלחה');
    }catch(err){
      alert('תקלה בשמירת הנתונים');
  }
}
const addConstraint = async (donor:Donor) => {
  await addDriverConstraints({date: date, driverId: donor.id, startLocation: donor.address, endHour: "0:00", requests: "",driverPhone:donor.phoneNumber,driverFirstName:donor.firstName,driverLastName:donor.lastName,startHour:"0:00"});
  fetchDrivers();
}
const removeRoute = async(index:number)=>{
  const updatedRoutes = [...data.routes];
  const route=updatedRoutes[index];
  await deleteRoute(route.routeId);
  await getDrops()
}

const handleWhatsAppShare = (route: Route) => {
  const text = formatRouteForCopy(route);
  const driverVisit = route.visit.find(v => v.status === "Start");
  if (driverVisit) {
    // Remove leading 0 and add +972
    const phoneNumber = '+972' + driverVisit.phoneNumber.replace(/^0/, '');
    const encodedText = encodeURIComponent(text);
    window.open(`https://wa.me/${phoneNumber}?text=${encodedText}`, '_blank');
  }
};


 return (
  <div style={{backgroundColor: "snow", height: '100vh', display: 'flex', flexDirection: 'column'}}>
    <DiveHeader/>
    
    <Box sx={{
      display: 'flex', 
      flexDirection: 'column', 
      alignItems: 'center', 
      gap: 2,
      marginBottom: '20px'
    }}>
      <ResponsiveDatePickers onDateChange={handleDateChange}/>
      
      <Box sx={{
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        gap: 2
      }}>
        <Button 
          variant="contained" 
          color="primary" 
          onClick={()=>setVisible(true)}
        >
          הוסף נהג
        </Button>
        
        {!data.routes.every((route)=>route.submitted) && (
          <Button 
            variant="contained" 
            color="primary" 
            onClick={handlePublishAll}
          >
            פרסם הכל
          </Button>
        )}
      </Box>
      
      {visible && (
        <AddDriverOption 
          donors={donors} 
          onClose={()=>setVisible(false)} 
          onClick={addConstraint}
        />
      )}
    </Box>
    <DndContext 
      sensors={sensors} 
      collisionDetection={closestCenter} 
      onDragEnd={handleDragEnd}
    >
      <Container 
        maxWidth="lg" 
        sx={{
          flex: 1, 
          display: 'flex', 
          overflow: 'hidden', 
          marginTop: '10px'
        }}
      >
        <Box 
          display="flex" 
          justifyContent="space-between" 
          gap={2} 
          sx={{width: '100%', overflow: 'hidden'}}
        >
          {/* Pickup Container */}
          <Box 
            flex={2} 
            sx={{
              marginTop: '5px', 
              overflowY: 'auto', 
              paddingRight: '10px'
            }}
          >
            <Typography variant="h5" align="center">
              טבחים
            </Typography>
            <SortableContext 
              items={data.pickup.map((_, idx) => `pickup-${idx}`)} 
              strategy={verticalListSortingStrategy}
            >
              {data.pickup.map((visit, index) => (
                <Draggable key={`pickup-${index}`} id={`pickup-${index}`}>
                  {renderVisit(visit)}
                </Draggable>
              ))}
            </SortableContext>
          </Box>

          {/* Routes Container */}
          <Box 
            flex={8} 
            sx={{
              marginTop: '5px', 
              width: '60%', 
              overflowY: 'auto', 
              paddingRight: '10px'
            }}
          >
            <Typography variant="h5" align="center">
              מסלולים
            </Typography>
            {data.routes.map((route, index) => (
              <Droppable 
                key={`route-${index}`} 
                id={`routes-${index}-visit-${route.visit.length+1}`}
              >
                <Card 
                  style={{ 
                    marginBottom: '16px', 
                    padding: '8px',
                    maxHeight: minimizedRoutes[index] ? '60px' : 'none',
                    overflow: 'hidden'
                  }}
                >
                 <Box 
  sx={{ 
    display: 'flex', 
    justifyContent: 'space-between', 
    alignItems: 'center' 
  }}
>
  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
    <Typography variant="h6">סיבוב {index+1}</Typography>
    {minimizedRoutes[index] && (
      <Typography variant="body2" color="text.secondary">
        {getVisitNamesForRoute(route)}
      </Typography>
    )}
  </Box>
  <IconButton onClick={() => toggleRouteMinimization(index)}>
    {minimizedRoutes[index] ? <ExpandMoreIcon /> : <ExpandLessIcon />}
  </IconButton>
</Box>
                  {!minimizedRoutes[index] && (
                    <>
                      <Select
                        value={route.driverId||'לא נבחר'}
                        label="Driver"
                        onChange={async(e:any) => {await handleDriverChange(e, index)}}
                      >
                        {driver.map((driver) => (
                          <MenuItem 
                            key={driver.driverId} 
                            value={driver.driverId}
                          >
                            {driver.driverFirstName+' '+driver.driverLastName}
                          </MenuItem>
                        ))}
                        <MenuItem key="0" value="לא נבחר">לא נבחר</MenuItem>
                      </Select>
                      
                      <Typography variant="body2">
                        {route.submitted===true?"פורסם":"טרם פורסם"}
                      </Typography>
                      
                      <Button 
                        variant="contained" 
                        color="error" 
                        onClick={()=>removeRoute(index)}
                      >
                        מחק
                      </Button>

                      {route.submitted ? (
                        <Box sx={{ display: 'flex', gap: 1 }}>
                          <Button 
                            variant="contained" 
                            color="secondary" 
                            onClick={() => handleCopyRoute(route)}
                            data-no-drag
                          >
                            העתק מסלול
                          </Button>
                          <IconButton 
                            color="success" 
                            onClick={() => handleWhatsAppShare(route)}
                            data-no-drag
                          >
                            <WhatsAppIcon />
                          </IconButton>
                        </Box>
                      ) : (
                        <Button 
                          variant="contained" 
                          color="primary" 
                          onClick={() => handlePublish(index)}
                        >
                          פרסם
                        </Button>
                      )}

                      <SortableContext
                        items={route.visit.map((_, idx) => `route-${index}-visit-${idx}`)}
                        strategy={verticalListSortingStrategy}
                      >
                        {route.visit.map((visit, idx) => (
                          <Draggable 
                            key={`route-${index}-visit-${idx}`} 
                            id={`route-${index}-visit-${idx}`}
                          >
                            {renderVisit(visit)}
                            {upButton(index,idx,route)}
                            {downButton(index,idx,route)}
                          </Draggable> 
                        ))}       
                      </SortableContext>
                    </>
                  )}
                </Card>
              </Droppable>
            ))}
            
            <Fab color="primary" aria-label="add" onClick={handleAddRoute}>
              <AddIcon />
            </Fab>
          </Box>

          {/* Drop Container */}
          <Box 
            flex={2} 
            sx={{
              marginTop: '5px', 
              overflowY: 'auto', 
              paddingRight: '10px'
            }}
          >
            <Typography variant="h5" align="center">
              נזקקים
            </Typography>
            <SortableContext 
              items={data.drop.map((_, idx) => `drop-${idx}`)} 
              strategy={verticalListSortingStrategy}
            >
              {data.drop.map((visit, index) => (
                <Draggable key={`drop-${index}`} id={`drop-${index}`}>
                  {renderVisit(visit)}
                </Draggable>
              ))}
            </SortableContext>
          </Box>
        </Box>
      </Container>
    </DndContext>
  </div>
);
};

export default DrivingManager;
