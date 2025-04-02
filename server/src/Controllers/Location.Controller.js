import locationModel from "../Models/LangLong.Model.js"
import restaurantModel from "../Models/Restaurant.Model.js"
import userModel from "../Models/Users.Model.js"

const getLocation = async (req, resp) => {
    try {
        const userId = req.user;
    
        if (!userId) {
            return resp.status(400).json({ message: "User id is required" });
        }

        const { lat = '', lang = '', address = '' } = req.body;
        
        if ([lat, lang, address].some(field => field.trim() === "")) {
            return resp.status(400).json({ message: "Lat, Lang, and Address are required" });
        }

        const latitude = Number(lat);
        const longitude = Number(lang);
       
        if (isNaN(latitude) || isNaN(longitude)) {
            return resp.status(400).json({ message: "Lat and Lang must be valid numbers" });
        }     

        
        const saveLocationAddress = new locationModel({
            Latitude: latitude,
            Longitude: longitude,
            Address: address
        });

       
        const location = await saveLocationAddress.save();

        if (!location) {
            return resp.status(500).json({ message: "Something went wrong while saving the location" });
        }

        const saveAddressToUser = await userModel.findByIdAndUpdate(userId._id,{address:location._id});

        console.log(saveAddressToUser);
        if(!saveAddressToUser)
        {
            return resp.status(404).send("Unable To push in user address");
        }

        resp.status(200).json({ message: "Location Saved Successfully" });

    } catch (error) {
        console.error(error);
        resp.status(500).json({ message: "Internal Server Error", error });
    }
}

//Updating Restaurants Location/Address
const updateRestaurantAddress = async (req,resp) => {
    const {restaurantId} = req.params;

    if(!restaurantId)
    {
        return resp.status(404).json({"message":"Restaurant Id is Required"})
    }

    const findRestaurantById = await restaurantModel.findById(restaurantId)

    if(!findRestaurantById)
    {
        return resp.status(404).json({"message":"Restaurant Didn't Exist"})
    }

    const addressReferenceId = findRestaurantById.addressRestaurant;

    if(!addressReferenceId)
    {
        return resp.status(404).json({"message":"Restaurant Didn't have any address"})
    }

    const {latitude,longitude} = req.body;

    if(!latitude && !longitude)
    {
        return resp.status(404).json({"message":"Langitude and Longitude Required For Address Updation"})
    }

    const updateLocation = await locationModel.findByIdAndUpdate(addressReferenceId,{$set:{"location.coordinates":[longitude,latitude]}},{new:true})

    if(!updateLocation)
    {
        return resp.status(400).json({"message":"SOmething Went Wrong in Updation"})
    }

    return resp.status(200).json({"message":"Address Successfully updated"})
}

export default getLocation;