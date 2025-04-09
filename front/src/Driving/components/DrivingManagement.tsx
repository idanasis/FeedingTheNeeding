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
import { Box, Card, CardContent, Typography, Select, MenuItem, Container, Button, IconButton, Fab,TextField, InputAdornment  } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
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
import { rejectCookRequest } from '../../Cooking/RestAPI/CookManagerRestAPI';
import DiveHeader from '../../GoPage/DiveHeader';
// Add these imports at the top of the file
import LocalDiningIcon from '@mui/icons-material/LocalDining';
import VolunteerActivismIcon from '@mui/icons-material/VolunteerActivism';
import DriverIcon from '@mui/icons-material/DriveEta';
import DeleteIcon from '@mui/icons-material/Delete';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import ClearIcon from '@mui/icons-material/Clear';


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
  const [chefSearchQuery, setChefSearchQuery] = useState<string>('');
  const [recipientSearchQuery, setRecipientSearchQuery] = useState<string>('');
  const [areAllRoutesMinimized, setAreAllRoutesMinimized] = useState<boolean>(true);
  const [selectedRecipientStreet, setSelectedRecipientStreet] = useState<string>('');

  const BEER_SHEVA_STREETS = [
  'העיר העתיקה', 'נווה עופר', 'המרכז האזרחי', 'א\'', 'ב\'', 'ג\'', 'ד\'', 
  'ה\'', 'ו\'', 'ט\'', 'י"א', 'נאות לון', 'נווה זאב', 'נווה נוי', 'נחל בקע', 
  'נחל עשן (נווה מנחם)', 'רמות', 'נאות אברהם (פלח 6)', 'נווה אילן (פלח 7)', 
  'הכלניות', 'סיגליות', 'פארק הנחל'
];
const [selectedStreet, setSelectedStreet] = useState<string>('');


   const toggleRouteMinimization = (index: number) => {
    setMinimizedRoutes(prev => ({
      ...prev,
      [index]: !prev[index]
    }));
  };

  const toggleAllRoutes = () => {
  setAreAllRoutesMinimized(prev => !prev);
  const newMinimizedState = !areAllRoutesMinimized;
  const newMinimizedRoutes = data.routes.reduce((acc, _, index) => {
    acc[index] = newMinimizedState;
    return acc;
  }, {} as {[key: number]: boolean});
  setMinimizedRoutes(newMinimizedRoutes);
};

const handleRemoveChef = (index: number,constraintId:number) => {

  rejectCookRequest(constraintId);

  const updatedPickup = [...data.pickup];
  updatedPickup.splice(index, 1);

  setData({
    ...data,
    pickup: updatedPickup
  });

};

  async function fetchDrivers(currentDate:Date=date) {
    try{
      const data=await getDriversConstraints(currentDate); 
      console.log(data);
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
        console.log(routes);
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
            route.visit.some((v: Visit) => v.constraintId === visit.constraintId)
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

  useEffect(() => {
  // Initialize routes as minimized only when routes are first loaded
  if (data.routes.length > 0) {
    const initialMinimizedRoutes = data.routes.reduce((acc, _, index) => {
      acc[index] = true;
      return acc;
    }, {} as {[key: number]: boolean});
    setMinimizedRoutes(initialMinimizedRoutes);
  }
}, [data.routes.length]);

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
      text += `כתובת: ${visit.street}, ${visit.address}\n`;
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

// Add this handler function in the DrivingManager component
const handleRemoveVisit = async (routeIndex: number, visitIndex: number) => {
  try {
    const updatedRoutes = [...data.routes];
    const route = updatedRoutes[routeIndex];
    
    // Remove the visit from the route
    const removedVisit = route.visit.splice(visitIndex, 1)[0];
    
    // Update priorities for remaining visits in the route
    for (let i = visitIndex; i < route.visit.length; i++) {
      if (route.visit[i].priority !== undefined) {
        route.visit[i].priority = (route.visit[i].priority as number) - 1;
      }
    }
    
    // Save the updated route
    await updateRoute(route);
    
    // Update data state with the updated routes
    const updatedData = { ...data, routes: updatedRoutes };
    
    // Add the visit back to the appropriate container based on its status
    if (removedVisit.status === "Pickup") {
      updatedData.pickup = [...data.pickup, removedVisit];
    } else if (removedVisit.status === "Deliver") {
      updatedData.drop = [...data.drop, removedVisit];
    }
    // Don't add back if it's a driver start point (status === "Start")
    
    setData(updatedData);
  } catch (err) {
    alert('תקלה בהסרת הביקור מהמסלול');
  }
};


const renderVisit = (visit: Visit, routeIndex?: number, visitIndex?: number, container?: string) => {
  const isChef = visit.status === "Pickup";
  const isNeedyPerson = visit.status === "Deliver";
  const isDriverStart = visit.status === "Start";
  
  // Show remove button for chefs in pickup section and visits in routes (except driver start)
  const showRemoveButton = (container === "pickup" || routeIndex !== undefined) && 
                          !(routeIndex !== undefined && visitIndex !== undefined && isDriverStart);

  return (
    <Card
      variant="outlined"
      sx={{
        height: showRemoveButton ? '180px' : '160px',
        backgroundColor: isChef 
          ? 'rgba(76, 175, 80, 0.1)' 
          : isNeedyPerson 
            ? 'rgba(244, 67, 54, 0.1)' 
            : 'rgba(33, 150, 243, 0.1)',
        border: isChef 
          ? '1px solid rgba(76, 175, 80, 0.5)' 
          : isNeedyPerson 
            ? '1px solid rgba(244, 67, 54, 0.5)' 
            : '1px solid rgba(33, 150, 243, 0.5)',
        position: 'relative',
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
        <Typography variant="body2" fontSize={11}>{visit.street}</Typography>
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
        
        
       {showRemoveButton && (
          <Box sx={{ position: 'absolute', bottom: '8px', left: '8px' }}>
            <IconButton 
              color="error" 
              size="small" 
              data-no-drag 
              onClick={(e) => {
                e.stopPropagation();
                if (container === "pickup") {
                  handleRemoveChef(visitIndex as number,visit.constraintId as number);
                } else if (routeIndex !== undefined && visitIndex !== undefined) {
                  handleRemoveVisit(routeIndex, visitIndex);
                }
              }}
            >
              <DeleteIcon fontSize="small" />
            </IconButton>
          </Box>
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
    const visit={address:updatedRoutes[index].driver?.startLocation as string,street:updatedRoutes[index].driver?.driverStreet || '', firstName:updatedRoutes[index].driver?.driverFirstName as string,lastName:updatedRoutes[index].driver?.driverLastName as string,phoneNumber:updatedRoutes[index].driver?.driverPhone as string,endHour:updatedRoutes[index].driver?.endHour,note:updatedRoutes[index].driver?.requests,status:"Start",priority:0,startHour:updatedRoutes[index].driver?.startHour};
    console.log(visit);
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
  await addDriverConstraints({date: date, driverId: donor.id, startLocation: donor.address, endHour: "0:00", requests: "",driverPhone:donor.phoneNumber,driverFirstName:donor.firstName,driverLastName:donor.lastName,startHour:"0:00",driverStreet: donor.street});
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

// Add this helper function
const filterVisit = (visit: Visit, searchQuery: string, streetFilter: string = '') => {
  // First check street filter
  if (streetFilter && (!visit.street || !visit.street.includes(streetFilter))) {
    return false;
  }
  
  // Then check search query
  if (searchQuery.trim()) {
    const query = searchQuery.toLowerCase();
    return visit.firstName.toLowerCase().includes(query) ||
      visit.lastName.toLowerCase().includes(query) ||
      visit.address.toLowerCase().includes(query) ||
      visit.phoneNumber.includes(query) ||
      (visit.note && visit.note.toLowerCase().includes(query)) ||
      (visit.notes && visit.notes.toLowerCase().includes(query));
  }
  
  return true;
};


// Add new search filter functions
const filterVisits = (visits: Visit[], searchQuery: string, streetFilter: string = '') => {
  let filtered = visits;
  
  // First apply text search if present
  if (searchQuery.trim()) {
    const query = searchQuery.toLowerCase();
    filtered = filtered.filter(visit => 
      visit.firstName.toLowerCase().includes(query) ||
      visit.lastName.toLowerCase().includes(query) ||
      visit.address.toLowerCase().includes(query) ||
      visit.phoneNumber.includes(query) ||
      (visit.note && visit.note.toLowerCase().includes(query)) ||
      (visit.notes && visit.notes.toLowerCase().includes(query))
    );
  }
  
  // Then apply street filter if selected
  if (streetFilter) {
    filtered = filtered.filter(visit => 
      visit.street && visit.street.includes(streetFilter)
    );
  }
  
  return filtered;
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
         {/* Pickup Container */}
          <Box 
            flex={2} 
            sx={{
              marginTop: '5px', 
              overflowY: 'auto', 
              paddingRight: '10px'
            }}
          >
            <Typography variant="h5" align="center" color="green">
              טבחים
            </Typography>
            {/* Add street filter dropdown for chefs */}
<Select
  fullWidth
  value={selectedStreet}
  onChange={(e) => setSelectedStreet(e.target.value as string)}
  displayEmpty
  variant="outlined"
  size="small"
  margin="dense"
  sx={{ marginBottom: 1 }}
>
  <MenuItem value="">כל השכונות</MenuItem>
  {BEER_SHEVA_STREETS.map((street) => (
    <MenuItem key={street} value={street}>
      {street}
    </MenuItem>
  ))}
</Select>
            {/* Add search field for chefs */}
            <TextField
              fullWidth
              variant="outlined"
              placeholder="חיפוש טבחים..."
              size="small"
              margin="normal"
              value={chefSearchQuery}
              onChange={(e) => setChefSearchQuery(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon />
                  </InputAdornment>
                ),
                endAdornment: chefSearchQuery ? (
                  <InputAdornment position="end">
                    <IconButton
                      size="small"
                      aria-label="clear search"
                      onClick={() => setChefSearchQuery('')}
                    >
                      <ClearIcon fontSize="small" />
                    </IconButton>
                  </InputAdornment>
                ) : null
              }}
            />

         <SortableContext 
          items={data.pickup.map((_, idx) => `pickup-${idx}`)} 
          strategy={verticalListSortingStrategy}
        >
          {data.pickup
            .map((visit, idx) => ({ visit, idx }))
            .filter(({ visit }) => filterVisit(visit, chefSearchQuery, selectedStreet))
            .map(({ visit, idx }) => (
              <Draggable key={`pickup-${idx}`} id={`pickup-${idx}`}>
                {renderVisit(visit, undefined, idx, "pickup")}
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
    <Typography variant="h6"   
                sx={{ 
                  color: route.submitted ? 'green' : 'red',
                  fontWeight: 'bold'
                }}>
                סיבוב {index+1}
    </Typography>
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
                          {route.submitted === true ? "פורסם" : "טרם פורסם"}
                        </Typography>

                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, flexWrap: 'wrap' }}>
                        <Button 
                          variant="contained" 
                          color="error" 
                          onClick={() => removeRoute(index)}
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
                      </Box>

                      <SortableContext
                        items={route.visit.map((_, idx) => `route-${index}-visit-${idx}`)}
                        strategy={verticalListSortingStrategy}
                      >
                       {route.visit.map((visit, idx) => (
                      <Draggable 
                        key={`route-${index}-visit-${idx}`} 
                        id={`route-${index}-visit-${idx}`}
                      >
                        {renderVisit(visit, index, idx)}
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
              הוסף
              מסלול
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
            <Typography variant="h5" align="center" color="red">
              נזקקים
            </Typography>

          {/* Add street filter dropdown for recipients */}
          <Select
            fullWidth
            value={selectedRecipientStreet}
            onChange={(e) => setSelectedRecipientStreet(e.target.value as string)}
            displayEmpty
            variant="outlined"
            size="small"
            margin="dense"
            sx={{ marginBottom: 1 }}
          >
            <MenuItem value="">כל השכונות</MenuItem>
            {BEER_SHEVA_STREETS.map((street) => (
              <MenuItem key={street} value={street}>
                {street}
              </MenuItem>
            ))}
          </Select>

            {/* Add search field for recipients */}
            <TextField
              fullWidth
              variant="outlined"
              placeholder="חיפוש נזקקים..."
              size="small"
              margin="normal"
              value={recipientSearchQuery}
              onChange={(e) => setRecipientSearchQuery(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon />
                  </InputAdornment>
                ),
                endAdornment: recipientSearchQuery ? (
                  <InputAdornment position="end">
                    <IconButton
                      size="small"
                      aria-label="clear search"
                      onClick={() => setRecipientSearchQuery('')}
                    >
                      <ClearIcon fontSize="small" />
                    </IconButton>
                  </InputAdornment>
                ) : null
              }}
            />

        <SortableContext 
          items={data.drop.map((_, idx) => `drop-${idx}`)} 
          strategy={verticalListSortingStrategy}
        >
          {data.drop
            .map((visit, idx) => ({ visit, idx }))
            .filter(({ visit }) => filterVisit(visit, recipientSearchQuery, selectedRecipientStreet))
            .map(({ visit, idx }) => (
              <Draggable key={`drop-${idx}`} id={`drop-${idx}`}>
                {renderVisit(visit, undefined, idx, "drop")}
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
