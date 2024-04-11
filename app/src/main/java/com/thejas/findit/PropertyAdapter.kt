import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thejas.findit.Property
import com.thejas.findit.R

class PropertyAdapter(private val properties: List<Property>) : RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>() {

    class PropertyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.property_name)
        val addressTextView: TextView = view.findViewById(R.id.property_address)
        val contactPersonTextView: TextView = view.findViewById(R.id.property_contact_person)
        val contactNumberTextView: TextView = view.findViewById(R.id.property_contact_number)
        val priceTextView: TextView = view.findViewById(R.id.property_price)
        val typeTextView: TextView = view.findViewById(R.id.property_type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_property, parent, false)
        return PropertyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = properties[position]
        holder.nameTextView.text = property.name
        holder.addressTextView.text = property.address
        holder.contactPersonTextView.text = property.contactPerson
        holder.contactNumberTextView.text = property.contactNumber
        holder.priceTextView.text = property.price.toString()
        holder.typeTextView.text = property.type
    }

    override fun getItemCount(): Int {
        return properties.size
    }
}