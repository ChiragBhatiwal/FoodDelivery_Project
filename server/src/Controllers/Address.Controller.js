import addressModel from "../Models/Address.Model.js"
import userModel from "../Models/Users.Model.js"

const addAddress = async (req,resp) => {

    const data = req.body;

    if(!data)
    {
        return console.log("nothing is in body");
    }

    const user = req.user;

    if(!user)
    {
      return console.log("user is not found");
    }

    const address = await addressModel.create(
      {
        addressLine:data.addressLine,
        landmark:data.landmark,
        cityName:data.cityName,
        postalCode:data.postalCode,
        stateName:data.stateName
      }
    );

    if(!address)
    {
      return console.log("address not saved");
    }

    const pushAddressInUser = await userModel.findByIdAndUpdate(
      user._id,
      {
        $push:{Address:address._id}
      },
      {
        new:true
      }
    );

    if(!pushAddressInUser)
    {
      return console.log("Can't push address in user");
    }
}

const deleteAddress = async (req,resp) => {
     const addressId = req.params;

     if(!addressId)
     {
      return console.log("address id is required");
     }

     const user = req.user;

     if(!user)
     {
       return console.log("user not found"); 
     }

     const deleteAddress = await addressModel.findByIdAndUpdate(addressId._id);

     if(!deleteAddress)
     {
      return console.log("Something went wrong while deleting address");
     }
 
     const removeAddressFromUser = await userModel.findByIdAndUpdate(
         user._id,
         {
          $pull:{Address:deleteAddress._id}
         }
     );

     if(!removeAddressFromUser)
     {
      return console.log("Adderss removed from user also");
     }

     resp.send("Address removed");
}

export {addAddress,deleteAddress};