import paymentModel from "../Models/Payment.Model.js"

const paymentDetails = async () => {

    const user = req.user;

    if(!user)
    {
        return console.log("user not found");
    }

    const itemId = req.params;

    if(!itemId)
    {
        return console.log("item id required");
    }

    const {paymentMethod,itemPrice} = req.body;

    if(!paymentMethod)
    {
      return console.log("payment methods also required");
    }

    const payment = await paymentModel.create(
        {
            itemPrice:itemPrice,
            paymentMethod:paymentMethod,
            userId:user._id,
            itemId:itemId._id
        }
    );

    if(!payment)
    {
        return console.log("Something went wrong in payment");
    }

    resp.send("payment successfull");

}

export default paymentDetails;